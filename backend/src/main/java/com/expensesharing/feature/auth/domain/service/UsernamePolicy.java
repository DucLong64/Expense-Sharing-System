package com.expensesharing.feature.auth.domain.service;

import com.expensesharing.common.exception.BusinessException;

import java.util.Set;
import java.util.regex.Pattern;

public final class UsernamePolicy {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-z0-9_.]{3,30}$");

    private static final Set<String> RESERVED_USERNAMES = Set.of(
            "admin", "administrator", "system", "support", "root", "api", "null", "undefined"
    );

    private UsernamePolicy() {
    }

    public static String normalize(String username) {
        if (username == null) {
            throw new BusinessException("INVALID_USERNAME", "Username is required.");
        }
        return username.trim().toLowerCase();
    }

    public static void validate(String normalizedUsername) {
        if (!USERNAME_PATTERN.matcher(normalizedUsername).matches()) {
            throw new BusinessException(
                    "INVALID_USERNAME",
                    "Username must be 3-30 characters and contain only lowercase letters, numbers, underscore, or dot."
            );
        }

        if (RESERVED_USERNAMES.contains(normalizedUsername)) {
            throw new BusinessException("INVALID_USERNAME", "Username is reserved.");
        }
    }
}
