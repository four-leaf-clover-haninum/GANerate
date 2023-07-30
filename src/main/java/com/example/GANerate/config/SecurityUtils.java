package com.example.GANerate.config;

import com.example.GANerate.enumuration.Result;
import com.example.GANerate.exception.CustomException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class SecurityUtils {

    private static List<SimpleGrantedAuthority> notUserAuthority = new ArrayList<>();

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new CustomException(Result.FAIL);
        }

        if (authentication.isAuthenticated()
                && !CollectionUtils.containsAny(
                authentication.getAuthorities(), notUserAuthority)) {
            return Long.valueOf(authentication.getName());
        }

        return 0L;
    }
}
