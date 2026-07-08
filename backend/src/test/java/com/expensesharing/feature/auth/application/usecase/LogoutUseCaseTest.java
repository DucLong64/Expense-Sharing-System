package com.expensesharing.feature.auth.application.usecase;

import com.expensesharing.feature.auth.application.port.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseTest {

    @Mock RefreshTokenRepository refreshTokenRepository;

    @InjectMocks LogoutUseCase logoutUseCase;

    @Test
    void logout_revokesRefreshToken() {
        logoutUseCase.execute("some_refresh_token");

        then(refreshTokenRepository).should().revoke("some_refresh_token");
    }
}
