package com.ceres.blip.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void sendNotification(String partnerCode, String title, String message, String type) {
        try {
            Map<String, String> payload = new HashMap<>();
            payload.put("partnerCode", partnerCode);
            payload.put("title", title);
            payload.put("message", message);
            payload.put("type", type);
            payload.put("timestamp", String.valueOf(System.currentTimeMillis()));

            String jsonMessage = objectMapper.writeValueAsString(payload);
            redisTemplate.convertAndSend("notifications", jsonMessage);
            log.info("Notification published to Redis for partner {}: {}", partnerCode, title);
        } catch (Exception e) {
            log.error("Failed to publish notification to Redis", e);
        }
    }
}
