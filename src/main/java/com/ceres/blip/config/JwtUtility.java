package com.ceres.blip.config;

import com.ceres.blip.models.database.SystemRoleModel;
import com.ceres.blip.models.database.SystemUserModel;
import com.ceres.blip.repositories.SystemRoleRepository;
import com.ceres.blip.repositories.SystemUserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtUtility {
    private static final String ROLE = "role";
    private static final String ROLE_CODE = "role_code";
    private static final String TYPE = "type";
    private static final String TOKEN_TYPE = "token_type";
    private static final String DOMAIN = "domain";
    private static final String PERMISSIONS = "permissions";
    private final SystemUserRepository userRepository;
    private final SystemRoleRepository roleRepository;

    @Value("${secret}")
    private String secret;

    public static final long JWT_TOKEN_VALIDITY = 12 * 60 * 60;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    public String generateToken(UserDetails userDetails, String tokenType, HttpServletRequest request) {
        return switch (tokenType) {
            case "ACCESS" -> generateAccessToken(new HashMap<>(), userDetails, request);
            case "REFRESH" -> generateRefreshToken(new HashMap<>(), userDetails);
            default -> throw new IllegalArgumentException("Invalid token type");
        };
    }

    public boolean isTokenValid(String token, UserDetails userDetails, HttpServletRequest request) {
        final String username = extractUsername(token);
        Claims claims = extractAllClaims(token);

        // Verify environment fingerprint matches
        String tokenFingerprint = claims.get("env_fingerprint", String.class);
        String currentFingerprint = generateEnvironmentFingerprint(request);

        if (!currentFingerprint.equals(tokenFingerprint)) {
            log.error("Environment fingerprint mismatch - Token was generated in different environment");
            return false;
        }

        // Additional verification: User Agent
        String tokenUserAgentHash = claims.get("user_agent_hash", String.class);
        String currentUserAgentHash = hashUserAgent(request);

        if (!currentUserAgentHash.equals(tokenUserAgentHash)) {
            log.error("User-Agent mismatch - Token used from different client");
            return false;
        }

        // Optional: IP verification (can be loosened for mobile networks)
        String tokenIpHash = claims.get("ip_hash", String.class);
        String currentIpHash = hashIpAddress(request);

        if (!currentIpHash.equals(tokenIpHash)) {
            log.error("IP address changed - Token used from different location");
            // You might want to make this a warning instead of rejection for mobile users
            return false;
        }

        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token, username);
    }

    /**
     * @param token jwt token
     * @param email username used when setting up userDetails, for our case, we use username
     * @return Boolean
     * @implNote Will check for two things. If the token is expired as per eat claim
     * and will also check of the user has already requested for another token, requesting for another token
     * makes all the previously generated tokens expired.
     */
    private boolean isTokenExpired(String token, String email) {
        SystemUserModel usersModel = userRepository.findByEmail(email).orElseThrow(IllegalStateException::new);
        if (usersModel == null) {
            throw new IllegalStateException("User not found");
        } else if (extractExpiration(token).before(new Date())) {
            throw new IllegalStateException("SESSION EXPIRED");
        } else if (usersModel.getLastLoggedInAt() == null) {
            throw new IllegalStateException("INVALID TOKEN");
        } else if (extractIssuedAt(token).after(usersModel.getLastLoggedInAt())) {
            throw new IllegalStateException("EXPIRED TOKEN USED");
        } else if (Boolean.FALSE.equals(usersModel.getIsActive())) {
            throw new IllegalStateException("ACCOUNT INACTIVE");
        }
        return false;
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateAccessToken(Map<String, Object> claims, UserDetails userDetails, HttpServletRequest request) {
        // truck the last time the token was generated -- the last time the user logged in
        long now = System.currentTimeMillis();
        Timestamp stamp = new Timestamp(now);

        SystemUserModel user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // archived staff members should not log in
        user.setLastLoggedInAt(stamp);
        userRepository.save(user);
        Optional<SystemRoleModel> rolesModel = roleRepository.findByRoleCode(user.getRoleCode());
        SystemRoleModel role = rolesModel.orElseThrow(() -> new IllegalStateException("User has no role assigned"));
        claims.put(ROLE, role.getRoleName());
        claims.put(TYPE, role.getRoleName());
        claims.put(TOKEN_TYPE, "ACCESS");
        claims.put(ROLE_CODE, role.getRoleCode());
        claims.put(DOMAIN, role.getRoleDomain());

        // Generate environment fingerprint
        String environmentFingerprint = generateEnvironmentFingerprint(request);

        claims.put("env_fingerprint", environmentFingerprint);
        claims.put("user_agent_hash", hashUserAgent(request));
        claims.put("ip_hash", hashIpAddress(request));

        List<String> permissions = new ArrayList<>();
        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            if (!permissions.contains(authority.getAuthority())) {
                permissions.add(authority.getAuthority());
            }
        }
        claims.put(PERMISSIONS, permissions);
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(user.getLastLoggedInAt())
                .setExpiration(new Date(now + JWT_TOKEN_VALIDITY * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Map<String, Object> claims, UserDetails userDetails) {
        long now = System.currentTimeMillis();

        SystemUserModel user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        claims.put("user", user.getUsername());
        claims.put(TOKEN_TYPE, "REFRESH");
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(user.getLastLoggedInAt())
                .setExpiration(new Date(now + JWT_TOKEN_VALIDITY * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> (String) claims.get(TOKEN_TYPE));
    }

    /**
     * Generate environment fingerprint combining multiple factors
     */
    private String generateEnvironmentFingerprint(@NonNull HttpServletRequest request) {
        StringBuilder fingerprint = new StringBuilder();

        // User-Agent (identifies browser/client type)
        String userAgent = request.getHeader("User-Agent");
        fingerprint.append(userAgent != null ? userAgent : "unknown");

        // Accept headers (browser vs API client differences)
        String accept = request.getHeader("Accept");
        fingerprint.append("|").append(accept != null ? accept : "");

        // Accept-Language (browser-specific)
        String acceptLanguage = request.getHeader("Accept-Language");
        fingerprint.append("|").append(acceptLanguage != null ? acceptLanguage : "");

        // Accept-Encoding (browser-specific)
        String acceptEncoding = request.getHeader("Accept-Encoding");
        fingerprint.append("|").append(acceptEncoding != null ? acceptEncoding : "");

        return hashString(fingerprint.toString());
    }

    /**
     * Hash User-Agent separately for additional verification
     */
    private String hashUserAgent(@NonNull HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return hashString(userAgent != null ? userAgent : "unknown");
    }

    /**
     * Hash IP address for location binding
     */
    private String hashIpAddress(@NonNull HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        return hashString(ipAddress);
    }

    /**
     * Get client IP address (handles proxies)
     */
    private String getClientIpAddress(@NonNull HttpServletRequest request) {
        String[] headerNames = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For can contain multiple IPs, take the first one
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * Hash string using SHA-256
     */
    private String hashString(@NonNull String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
