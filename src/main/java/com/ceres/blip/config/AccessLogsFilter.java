package com.ceres.blip.config;

import com.ceres.blip.models.database.AccessLogModel;
import com.ceres.blip.models.database.SystemUserModel;
import com.ceres.blip.repositories.AccessLogRepository;
import com.ceres.blip.repositories.SystemRoleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class AccessLogsFilter extends OncePerRequestFilter {
    private final AccessLogRepository accessLogRepository;
    private final SystemRoleRepository systemRoleRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        long currentTimeMillis = System.currentTimeMillis();

        String username = getUsername(request);

        // Proceed with the filter chain
        filterChain.doFilter(request, response);

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);


        String requestBody = new String(wrappedRequest.getContentAsByteArray(), wrappedRequest.getCharacterEncoding());
        String responseBody = new String(wrappedResponse.getContentAsByteArray(), wrappedResponse.getCharacterEncoding());

        wrappedResponse.copyBodyToResponse(); // important!
        long duration = System.currentTimeMillis() - currentTimeMillis;
        String uri = request.getRequestURI();
        int status = response.getStatus();

        String method = request.getMethod();
        String remoteAddr = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        SystemUserModel authenticatedUser = getAuthenticatedSystemUser();
        String partnerCode = null;
        if (authenticatedUser != null) {
            partnerCode = authenticatedUser.getPartnerCode();
        }

        AccessLogModel accessLog = new AccessLogModel();
        accessLog.setMethod(method);
        accessLog.setPath(request.getRequestURI());
        accessLog.setQueryParams(requestBody);
        accessLog.setAddress(remoteAddr);
        accessLog.setUserAgent(userAgent);
        accessLog.setContentType(request.getContentType());
        accessLog.setResponse(responseBody); // placeholder
        accessLog.setIsError(response.getStatus() >= 400);
        accessLog.setPartnerCode(partnerCode);
        accessLog.setClientInfo("{}"); // can populate with device info
        accessLog.setUsername(username);
        accessLog.setCreatedAt(new Timestamp(Instant.now().toEpochMilli()));
        accessLog.setIsError(false);

        boolean isLoginRequest = isLoginRequest(wrappedRequest);

        wrappedResponse.copyBodyToResponse();
    }

    private static String getUsername(HttpServletRequest request) {
        String username = request.getRemoteUser() != null ? request.getRemoteUser() : "anonymousUser";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
            && authentication.isAuthenticated()
            && !(authentication instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (userDetails != null && userDetails.getUsername() != null) {
                username = userDetails.getUsername();
            }
        }
        return username;
    }

    public boolean isLoginRequest(ContentCachingRequestWrapper requestWrapper) {
        String requestBody = new String(requestWrapper.getContentAsByteArray());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode requestJson = null;
        if (requestBody.isBlank()) {
            return false;
        }
        try {
            requestJson = mapper.readValue(requestBody, JsonNode.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        if (requestJson == null || requestJson.isEmpty()) {
            return false;
        }
        return requestJson.has("ACTION") && Objects.equals(requestJson.get("ACTION").asText(), "login");
    }

    private SystemUserModel getAuthenticatedSystemUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
            && authentication.isAuthenticated()
            && !(authentication instanceof AnonymousAuthenticationToken)) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof SystemUserModel) {
                return (SystemUserModel) principal;
            }
        }
        return null;
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
