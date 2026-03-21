package com.ceres.blip.annotations.handler;

import com.ceres.blip.annotations.base.RequiresAuthentication;
import com.ceres.blip.exceptions.AuthorizationRequiredException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class RequiresAuthenticationHandler {

    @Before("@annotation(requiresAuthentication)")
    public void requiresAuth(RequiresAuthentication requiresAuthentication) {
        try {
            if (Boolean.FALSE.equals(isAuthenticated())) {
                throw new AuthorizationRequiredException(requiresAuthentication.message());
            }
        } catch (AuthorizationRequiredException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public Boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return null != authentication
               && authentication.isAuthenticated()
               && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
