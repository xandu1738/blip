package com.ceres.blip.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class RedisConfiguration {
        private final SimpMessagingTemplate template;

        @Bean
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
                RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
                redisTemplate.setConnectionFactory(connectionFactory);
                return redisTemplate;
        }

        @Bean
        public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
                // Default cache configuration using RedisSerializer.json() (no arguments)
                RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .disableCachingNullValues()
                                .entryTtl(Duration.ofMinutes(60))
                                .serializeKeysWith(
                                                RedisSerializationContext.SerializationPair
                                                                .fromSerializer(RedisSerializer.string()))
                                .serializeValuesWith(
                                                RedisSerializationContext.SerializationPair
                                                                .fromSerializer(RedisSerializer.json()) // No arguments
                                                                                                        // - uses
                                                                                                        // default
                                                                                                        // ObjectMapper
                                );

                // Optional: Configure different TTLs for different caches
                Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

                // Short-lived cache (5 minutes)
                cacheConfigurations.put("shortCache",
                                defaultConfig.entryTtl(Duration.ofMinutes(5)));

                // Long-lived cache (24 hours)
                cacheConfigurations.put("longCache",
                                defaultConfig.entryTtl(Duration.ofHours(24)));

                // User session cache (30 minutes)
                cacheConfigurations.put("userCache",
                                defaultConfig.entryTtl(Duration.ofMinutes(30)));

                return RedisCacheManager.builder(redisConnectionFactory)
                                .cacheDefaults(defaultConfig)
                                .withInitialCacheConfigurations(cacheConfigurations)
                                .transactionAware() // Optional: enable transaction support
                                .build();
        }

        @Bean
        public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
                        ObjectMapper objectMapper) {
                RedisMessageListenerContainer container = new RedisMessageListenerContainer();
                container.setConnectionFactory(connectionFactory);

                // General events (like user registration - legacy or system-wide)
                container.addMessageListener((message, pattern) -> {
                        String payload = new String(message.getBody());
                        template.convertAndSend("/topic/events", payload);
                }, new PatternTopic("notification-channel"));

                // Partner-specific notifications
                container.addMessageListener((message, pattern) -> {
                        try {
                                String payload = new String(message.getBody());
                                // Remove quotes if the message was serialized as a string by RedisTemplate
                                if (payload.startsWith("\"") && payload.endsWith("\"")) {
                                        payload = payload.substring(1, payload.length() - 1).replace("\\\"", "\"");
                                }

                                // Use TypeReference for safe deserialization
                                Map<String, Object> data = objectMapper.readValue(payload,
                                                new TypeReference<Map<String, Object>>() {
                                                });
                                String partnerCode = (String) data.get("partnerCode");
                                if (partnerCode != null) {
                                        template.convertAndSend("/topic/notifications/" + partnerCode, payload);
                                }
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                }, new PatternTopic("notifications"));

                return container;
        }
}
