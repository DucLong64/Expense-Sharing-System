package com.expensesharing.feature.auth.infrastructure.persistence;

import com.expensesharing.feature.auth.application.port.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Override
    public void save(UUID userId, String token) {
        RefreshTokenJpaEntity entity = RefreshTokenJpaEntity.builder()
                .userId(userId)
                .token(token)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpiration))
                .revoked(false)
                .build();
        jpaRepository.save(entity);
    }

    @Override
    public Optional<UUID> findUserIdByToken(String token) {
        return jpaRepository.findValidToken(token).map(RefreshTokenJpaEntity::getUserId);
    }

    @Override
    public void revoke(String token) {
        jpaRepository.revokeByToken(token);
    }

    @Override
    public void revokeAllByUserId(UUID userId) {
        jpaRepository.revokeAllByUserId(userId);
    }
}
