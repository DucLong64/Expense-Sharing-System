package com.expensesharing.feature.auth.application.usecase;

import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.feature.auth.application.dto.AuthResult;
import com.expensesharing.feature.auth.application.port.RefreshTokenRepository;
import com.expensesharing.feature.auth.application.port.TokenService;
import com.expensesharing.feature.auth.domain.model.User;
import com.expensesharing.feature.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Transactional
    public AuthResult execute(String refreshToken) {
        UUID userId = refreshTokenRepository.findUserIdByToken(refreshToken)
                .orElseThrow(() -> new BusinessException("INVALID_REFRESH_TOKEN", "Refresh token is invalid or expired."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found."));

        refreshTokenRepository.revoke(refreshToken);

        String newAccessToken = tokenService.generateAccessToken(user.getId(), user.getEmail());
        String newRefreshToken = tokenService.generateRefreshToken(user.getId());
        refreshTokenRepository.save(userId, newRefreshToken);

        return new AuthResult(newAccessToken, newRefreshToken);
    }
}
