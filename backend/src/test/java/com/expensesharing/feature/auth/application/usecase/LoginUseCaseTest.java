package com.expensesharing.feature.auth.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.feature.auth.application.dto.AuthResult;
import com.expensesharing.feature.auth.application.dto.LoginCommand;
import com.expensesharing.feature.auth.application.port.RefreshTokenRepository;
import com.expensesharing.feature.auth.application.port.TokenService;
import com.expensesharing.feature.auth.domain.model.User;
import com.expensesharing.feature.auth.domain.repository.UserRepository;
import com.expensesharing.feature.auth.domain.service.PasswordEncoder;
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
class LoginUseCaseTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock TokenService tokenService;
    @Mock RefreshTokenRepository refreshTokenRepository;

    @InjectMocks LoginUseCase loginUseCase;

    private final User user = TestDataFactory.aUser();

    @Test
    void login_success() {
        var command = new LoginCommand("testuser", "password123");

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(passwordEncoder.matches(command.password(), user.getPassword())).willReturn(true);
        given(tokenService.generateAccessToken(any(), anyString())).willReturn("access_token");
        given(tokenService.generateRefreshToken(any())).willReturn("refresh_token");

        AuthResult result = loginUseCase.execute(command);

        assertThat(result.accessToken()).isEqualTo("access_token");
        assertThat(result.refreshToken()).isEqualTo("refresh_token");
    }

    @Test
    void login_usernameNotFound_throwsException() {
        var command = new LoginCommand("unknown", "password123");

        given(userRepository.findByUsername("unknown")).willReturn(Optional.empty());

        assertThatThrownBy(() -> loginUseCase.execute(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid username or password");
    }

    @Test
    void login_wrongPassword_throwsException() {
        var command = new LoginCommand("testuser", "wrong_password");

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(passwordEncoder.matches(command.password(), user.getPassword())).willReturn(false);

        assertThatThrownBy(() -> loginUseCase.execute(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid username or password");
    }
}
