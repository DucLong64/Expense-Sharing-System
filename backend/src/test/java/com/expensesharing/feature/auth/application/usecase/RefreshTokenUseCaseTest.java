package com.expensesharing.feature.auth.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.feature.auth.application.dto.AuthResult;
import com.expensesharing.feature.auth.application.port.RefreshTokenRepository;
import com.expensesharing.feature.auth.application.port.TokenService;
import com.expensesharing.feature.auth.domain.model.User;
import com.expensesharing.feature.auth.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseTest {

    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock TokenService tokenService;
    @Mock UserRepository userRepository;

    @InjectMocks RefreshTokenUseCase refreshTokenUseCase;

    private final User user = TestDataFactory.aUser();

    @Test
    void refresh_success() {
        given(refreshTokenRepository.findUserIdByToken("valid_token")).willReturn(Optional.of(user.getId()));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(tokenService.generateAccessToken(any(), anyString())).willReturn("new_access_token");
        given(tokenService.generateRefreshToken(any())).willReturn("new_refresh_token");

        AuthResult result = refreshTokenUseCase.execute("valid_token");

        assertThat(result.accessToken()).isEqualTo("new_access_token");
        assertThat(result.refreshToken()).isEqualTo("new_refresh_token");
        then(refreshTokenRepository).should().revoke("valid_token");
        then(refreshTokenRepository).should().save(eq(user.getId()), eq("new_refresh_token"));
    }

    @Test
    void refresh_invalidToken_throwsException() {
        given(refreshTokenRepository.findUserIdByToken("invalid_token")).willReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenUseCase.execute("invalid_token"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Refresh token is invalid or expired");
    }
}
