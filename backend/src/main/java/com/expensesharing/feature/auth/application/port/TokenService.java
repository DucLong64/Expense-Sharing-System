package com.expensesharing.feature.auth.application.port;

import java.util.UUID;

public interface TokenService {

    String generateAccessToken(UUID userId, String email);

    String generateRefreshToken(UUID userId);

    UUID extractUserId(String token);

    boolean isAccessTokenValid(String token);
}
