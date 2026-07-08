package com.expensesharing.feature.auth.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, Long> {

    @Query("SELECT r FROM RefreshTokenJpaEntity r WHERE r.token = :token AND r.revoked = false AND r.expiresAt > CURRENT_TIMESTAMP")
    Optional<RefreshTokenJpaEntity> findValidToken(String token);

    @Modifying
    @Query("UPDATE RefreshTokenJpaEntity r SET r.revoked = true WHERE r.token = :token")
    void revokeByToken(String token);

    @Modifying
    @Query("UPDATE RefreshTokenJpaEntity r SET r.revoked = true WHERE r.userId = :userId")
    void revokeAllByUserId(UUID userId);
}
