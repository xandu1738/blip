package com.ceres.blip.utils.events;

import com.ceres.blip.models.database.SystemUserModel;
import com.ceres.blip.utils.mail.MessagingService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.HashMap;
import java.util.Map;
import java.io.UnsupportedEncodingException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationEventListener implements ApplicationListener<UserRegistrationEvent> {
    private final MessagingService mailService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onApplicationEvent(UserRegistrationEvent event) {
        // Handle the event (e.g., send a confirmation email)
        SystemUserModel user = event.getUser();
        String password = event.getPassword();
        String applicationUrl = event.getApplicationUrl();

        // Publish to Redis
        try {
            Map<String, String> message = new HashMap<>();
            message.put("type", "USER_REGISTRATION");
            message.put("partnerCode",user.getPartnerCode());
            message.put("email", user.getEmail());
            message.put("firstName", user.getFirstName());
            message.put("lastName", user.getLastName());
            message.put("timestamp", String.valueOf(System.currentTimeMillis()));

            String jsonMessage = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend("notification-channel", jsonMessage);
            log.info("Published user registration event to Redis: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to publish registration event to Redis", e);
        }

        String mailBody = """
                <p>Dear %s %s,</p>
                <p>Your account has been successfully created. Below are your login details:</p>
                <ul>
                    <li><strong>Email:</strong> %s</li>
                    <li><strong>Password:</strong> %s</li>
                </ul>
                <p>You can log in to your account using the following link:</p>
                <p><a href="%s" target="_blank">Login to Your Account</a></p>
                <p>Please change your password after your first login for security purposes.</p>
                <p>Best regards,<br/>
                The Blip Team</p>
                """.formatted(user.getFirstName(), user.getLastName(), user.getEmail(), password, applicationUrl);

        mailService.sendTemplateMail(user, "Account Created Successfully", mailBody, "email-template");
    }
}
