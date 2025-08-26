package com.ceres.blip.utils.events;

import com.ceres.blip.models.database.SystemUserModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UserRegistrationEvent extends ApplicationEvent {
    private SystemUserModel user;
    private String applicationUrl;
    private String password;

    public UserRegistrationEvent(SystemUserModel user, String applicationUrl, String password) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
        this.password = password;
    }
}
