package com.expensesharing.feature.auth.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class User {

    private final UUID id;
    private final String username;
    private final String email;
    private String password;
    private final String fullName;
    private final Instant createdAt;

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
