package com.pablogb.psychologger.security;

import com.pablogb.psychologger.model.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    public Integer getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public Integer getCurrentOrgId() {
        return getCurrentUser().getOrganization().getId();
    }
}
