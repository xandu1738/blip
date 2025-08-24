package com.ceres.project.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ceres.project.models.database.AccessLogModel;
import com.ceres.project.models.database.SystemRoleModel;
import com.ceres.project.models.database.SystemUserModel;
import com.ceres.project.repositories.AccessLogRepository;
import com.ceres.project.repositories.SystemRoleRepository;
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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        String username = request.getRemoteUser() != null ? request.getRemoteUser() : "anonymousUser";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
            && authentication.isAuthenticated()
            && !(authentication instanceof AnonymousAuthenticationToken)){
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (userDetails != null && userDetails.getUsername() != null) {
                username = userDetails.getUsername();
            }
        }

        // Proceed with the filter chain
        filterChain.doFilter(request, response);

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);


        String requestBody = new String(wrappedRequest.getContentAsByteArray(), wrappedRequest.getCharacterEncoding());
        String responseBody = new String(wrappedResponse.getContentAsByteArray(), wrappedResponse.getCharacterEncoding());

        wrappedResponse.copyBodyToResponse(); // important!
        long duration = System.currentTimeMillis() - currentTimeMillis;
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String remoteAddr = request.getRemoteAddr();
        int status = response.getStatus();

        AccessLogModel accessLog = new AccessLogModel();
        accessLog.setMethod(request.getMethod());
        accessLog.setPath(request.getRequestURI());
        accessLog.setQueryParams(requestBody);
        accessLog.setAddress(getClientIp(request));
        accessLog.setUserAgent(request.getHeader("User-Agent"));
        accessLog.setContentType(request.getContentType());
        accessLog.setResponse(responseBody); // placeholder
        accessLog.setIsError(response.getStatus() >= 400);
        accessLog.setPartnerCode(request.getHeader("X-Partner-Code"));
        accessLog.setClientInfo("{}"); // can populate with device info
        accessLog.setUsername(username);
        accessLog.setCreatedAt(new Timestamp(Instant.now().toEpochMilli()));
        accessLog.setIsError(false);


        boolean isLoginRequest = isLoginRequest(wrappedRequest);


        SystemUserModel user = null;
        if (!isLoginRequest) {
            user = (SystemUserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (user != null) {
                    SystemRoleModel role = systemRoleRepository.findByRoleCode(user.getRoleCode()).orElseThrow(() -> new IllegalStateException("Couldn't resolve Role"));
//                only log requests from web users
//                    if (Objects.equals(role.getRoleDomain(), AppDomains.CLIENT)) {
                        wrappedResponse.copyBodyToResponse();
                        return;
//                    }
                }
        }

        JSONObject responseJson = JSON.parseObject(responseBody);
        JSONObject requestJson = null;

        if (!isLoginRequest && user != null) {
            accessLog.setUsername(user.getEmail());
            accessLog.setRequest(
                    requestJson != null ? requestJson.toString() :
                            Stream.of(wrappedRequest.getParameterMap())
                                    .flatMap(map -> map.entrySet().stream())
                                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> String.join(" ", entry.getValue())))
                                    .toString()
            );
        } else {
            if (requestJson == null || !requestJson.containsKey("email")) {
                wrappedResponse.copyBodyToResponse();
                return;
            }
            accessLog.setUsername(requestJson.getString("email"));
            accessLog.setRequest("Login");
        }

        accessLog.setResponse(JSON.toJSONString(responseJson));

        if (wrappedResponse.getStatus() >= 300 || wrappedResponse.getStatus() < 200) {
            accessLog.setResponse(responseJson.getString("returnMessage") == null ? responseJson.getString("message")
                    : responseJson.getString("returnMessage"));
            accessLog.setIsError(true);
        }

        if (responseJson != null && responseJson.containsKey("returnMessage") && responseJson.getString("returnMessage") != null) {
            accessLog.setResponse(responseJson.getString("returnMessage") == null ?
                    (responseJson.toString().length() > 256 ? responseJson.toString().substring(0, 240).concat("(truncated)...") :
                            responseJson.toString()) : responseJson.getString("returnMessage"));
        }
        // Save the access log to the database
//        accessLogRepository.save(accessLog);

        // Log the access details
        log.info("Access Log: Method={}, URI={}, RemoteAddr={}, Status={}, Duration={}ms", method, uri, remoteAddr, status, duration);
    }

    public boolean isLoginRequest(ContentCachingRequestWrapper requestWrapper) {
        String requestBody = new String(requestWrapper.getContentAsByteArray());
        return requestBody.contains("\"ACTION\": \"login\"");
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
