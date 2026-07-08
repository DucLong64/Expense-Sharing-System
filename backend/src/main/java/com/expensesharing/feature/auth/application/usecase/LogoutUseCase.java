package com.expensesharing.feature.auth.application.usecase;

import com.expensesharing.feature.auth.application.port.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void execute(String refreshToken) {
        refreshTokenRepository.revoke(refreshToken);
    }
}
