package org.example.touragency.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.example.touragency.exception.ForbiddenException;

import java.util.UUID;

public class SecurityUtils {

    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new ForbiddenException("User is not authenticated");
        }
        return (UUID) authentication.getPrincipal();
    }
}