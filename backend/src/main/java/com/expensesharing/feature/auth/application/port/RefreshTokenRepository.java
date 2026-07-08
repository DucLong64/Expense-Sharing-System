package com.expensesharing.feature.auth.application.port;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {

    void save(UUID userId, String token);

    Optional<UUID> findUserIdByToken(String token);

    void revoke(String token);

    void revokeAllByUserId(UUID userId);
}
