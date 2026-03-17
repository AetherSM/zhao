package org.example.zhao.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.stream.Collectors;

public class SecurityUtil {

    public static LoginUser currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof LoginUser) return (LoginUser) principal;
        return null;
    }

    public static Long currentUserId() {
        LoginUser u = currentUser();
        return u == null ? null : u.getUserId();
    }

    public static Set<String> currentRoleCodes() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return Set.of();
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    public static boolean hasRole(String roleCode) {
        return currentRoleCodes().contains("ROLE_" + roleCode);
    }
}

