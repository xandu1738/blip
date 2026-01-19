package com.ceres.blip.config;

import com.ceres.blip.models.database.AccessLogModel;
import com.ceres.blip.models.database.SystemUserModel;
import com.ceres.blip.repositories.SystemUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AuditTrailFilter extends OncePerRequestFilter {

    private static final int MAX_PAYLOAD_LENGTH = 10000; // 10KB
    private static final int CONTENT_CACHE_LIMIT = 10240; // 10KB
    private final JwtUtility jwtUtility;
    private final SystemUserRepository systemUserRepository;

    public AuditTrailFilter(JwtUtility jwtUtility, SystemUserRepository systemUserRepository) {
        this.jwtUtility = jwtUtility;
        this.systemUserRepository = systemUserRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Skip logging for static resources and health checks
        if (shouldNotLog(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Generate correlation ID for request tracking
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        MDC.put("requestUri", request.getRequestURI());

        Instant startTime = Instant.now();

        // Wrap request and response to cache content for logging
        ContentCachingRequestWrapper wrappedRequest =
                new ContentCachingRequestWrapper(request, CONTENT_CACHE_LIMIT);
        ContentCachingResponseWrapper wrappedResponse =
                new ContentCachingResponseWrapper(response);

        AccessLogModel accessLog = new AccessLogModel();
        accessLog.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        try {
            // Log incoming request
            logRequest(wrappedRequest, accessLog);

            // Process the request
            filterChain.doFilter(wrappedRequest, wrappedResponse);

        } finally {
            // Calculate duration
            Duration duration = Duration.between(startTime, Instant.now());

            // Log response
            logResponse(wrappedResponse, duration, accessLog);

            // CRITICAL: Copy body to actual response
            wrappedResponse.copyBodyToResponse();

            // Clean up MDC
            MDC.clear();
        }
    }

    /**
     * Log incoming request details
     */
    private void logRequest(ContentCachingRequestWrapper request, AccessLogModel accessLog) {
        try {
            String userAgent = request.getHeader("User-Agent");
            String queryString = request.getQueryString();
            String clientIpAddress = getClientIpAddress(request);

            accessLog.setMethod(request.getMethod());
            accessLog.setAddress(clientIpAddress);
            accessLog.setPath(request.getRequestURI());

//            accessLog.setClientInfo();
            accessLog.setContentType(request.getContentType());

            accessLog.setRequest(queryString);

            Optional<SystemUserModel> loggedInUser = getLoggedInUser(request);
            if (loggedInUser.isPresent()) {
                accessLog.setPartnerCode(loggedInUser.get().getPartnerCode());
                accessLog.setUsername(loggedInUser.get().getEmail());
            }

            accessLog.setUserAgent(userAgent);

            // Log request body for POST/PUT/PATCH
            if (hasBody(request.getMethod())) {
                String requestBody = getRequestBody(request);
                if (requestBody != null && !requestBody.isEmpty()) {
                    accessLog.setRequest(requestBody);
                }
            }
        } catch (Exception e) {
            log.error("Error logging request", e);
        }
    }

    private Optional<SystemUserModel> getLoggedInUser(ContentCachingRequestWrapper request) {
        //Get Logged in user from Bearer token
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.isBlank(authHeader)) {
            return Optional.empty();
        }
        authHeader = authHeader.replace("Bearer ", "");
        String username = jwtUtility.extractUsername(authHeader);

        return systemUserRepository.findByEmail(username);
    }

    /**
     * Log outgoing response details
     */
    private void logResponse(ContentCachingResponseWrapper response, Duration duration, AccessLogModel accessLog) {
        try {

            // Log response body for non-binary responses
            if (shouldLogResponseBody(response)) {
                String responseBody = getResponseBody(response);
                if (responseBody != null && !responseBody.isEmpty()) {

                    log.info("Response took {}", duration);
                    accessLog.setResponse(responseBody);
                    //FIXME: Include the duration in the access log data saved.

                    if (response.getStatus() >= 400) {
                        accessLog.setIsError(true);
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error logging response", e);
        }
    }

    /**
     * Extract request body from cached content
     */
    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            String body = new String(content, StandardCharsets.UTF_8);
            return body.length() > MAX_PAYLOAD_LENGTH
                    ? body.substring(0, MAX_PAYLOAD_LENGTH) + "... (truncated)"
                    : body;
        }
        return null;
    }

    /**
     * Extract response body from cached content
     */
    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            String body = new String(content, StandardCharsets.UTF_8);
            return body.length() > MAX_PAYLOAD_LENGTH
                    ? body.substring(0, MAX_PAYLOAD_LENGTH) + "... (truncated)"
                    : body;
        }
        return null;
    }

    /**
     * Format JSON payload with indentation
     */
    private String formatJsonPayload(String payload, String prefix) {
        try {
            // Try to pretty-print JSON
            if (payload.trim().startsWith("{") || payload.trim().startsWith("[")) {
                return payload.lines()
                               .map(line -> prefix + line)
                               .collect(Collectors.joining("\n")) + "\n";
            }
        } catch (Exception e) {
            // If not JSON, return as-is
        }
        return prefix + payload + "\n";
    }

    /**
     * Get client IP address (handles proxies)
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
                "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP",
                "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

    /**
     * Check if request method has body
     */
    private boolean hasBody(String method) {
        return "POST".equals(method) || "PUT".equals(method) ||
               "PATCH".equals(method) || "DELETE".equals(method);
    }

    /**
     * Check if header should be logged
     */
    private boolean shouldLogHeader(String headerName) {
        String lower = headerName.toLowerCase();
        // Exclude sensitive headers
        return !lower.contains("authorization") &&
               !lower.contains("cookie") &&
               !lower.contains("token") &&
               !lower.contains("secret") &&
               !lower.contains("password");
    }

    /**
     * Mask sensitive header values
     */
    private String maskSensitiveHeader(String headerName, String value) {
        if (value == null) return null;

        String lower = headerName.toLowerCase();
        if (lower.contains("authorization") || lower.contains("token")) {
            return "***MASKED***";
        }
        return value;
    }

    /**
     * Check if response body should be logged
     */
    private boolean shouldLogResponseBody(ContentCachingResponseWrapper response) {
        String contentType = response.getContentType();
        if (contentType == null) return false;

        // Only log text-based responses
        return contentType.contains("json") ||
               contentType.contains("xml") ||
               contentType.contains("text") ||
               contentType.contains("html");
    }

    /**
     * Determine if the request should be logged
     */
    private boolean shouldNotLog(HttpServletRequest request) {
        String uri = request.getRequestURI();

        // Skip static resources
        if (uri.matches(".+\\.(css|js|jpg|jpeg|png|gif|ico|woff|woff2|ttf|svg)$")) {
            return true;
        }

        // Skip actuator endpoints
        if (uri.startsWith("/actuator/")) {
            return true;
        }

        // Skip health checks
        return uri.equals("/health") || uri.equals("/ping");
    }
}
