package com.ceres.blip.utils.events;

import com.ceres.blip.models.database.SystemUserModel;
import com.ceres.blip.utils.mail.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
@RequiredArgsConstructor
public class RegistrationEventListener implements ApplicationListener<UserRegistrationEvent> {
    private final MailService mailService;

    @Override
    public void onApplicationEvent(UserRegistrationEvent event) {
        // Handle the event (e.g., send a confirmation email)
        try {
            SystemUserModel user = event.getUser();
            String password = event.getPassword();
            String applicationUrl = event.getApplicationUrl();

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
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}
