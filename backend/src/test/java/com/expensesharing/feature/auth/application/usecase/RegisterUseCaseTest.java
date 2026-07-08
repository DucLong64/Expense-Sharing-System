package com.expensesharing.feature.auth.application.usecase;

import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.feature.auth.application.dto.AuthResult;
import com.expensesharing.feature.auth.application.dto.RegisterCommand;
import com.expensesharing.feature.auth.application.port.RefreshTokenRepository;
import com.expensesharing.feature.auth.application.port.TokenService;
import com.expensesharing.feature.auth.domain.repository.UserRepository;
import com.expensesharing.feature.auth.domain.service.PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUseCaseTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock TokenService tokenService;
    @Mock RefreshTokenRepository refreshTokenRepository;

    @InjectMocks RegisterUseCase registerUseCase;

    @Test
    void register_success() {
        var command = new RegisterCommand("testuser", "test@example.com", "password123", "Test User");

        given(userRepository.existsByUsername("testuser")).willReturn(false);
        given(userRepository.existsByEmail("test@example.com")).willReturn(false);
        given(passwordEncoder.encode(command.password())).willReturn("encoded");
        given(tokenService.generateAccessToken(any(), anyString())).willReturn("access_token");
        given(tokenService.generateRefreshToken(any())).willReturn("refresh_token");

        AuthResult result = registerUseCase.execute(command);

        assertThat(result.accessToken()).isEqualTo("access_token");
        assertThat(result.refreshToken()).isEqualTo("refresh_token");
        then(userRepository).should().save(any());
        then(refreshTokenRepository).should().save(any(), eq("refresh_token"));
    }

    @Test
    void register_usernameAlreadyExists_throwsException() {
        var command = new RegisterCommand("testuser", "test@example.com", "password123", "Test User");

        given(userRepository.existsByUsername("testuser")).willReturn(true);

        assertThatThrownBy(() -> registerUseCase.execute(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Username already in use");
    }

    @Test
    void register_emailAlreadyExists_throwsException() {
        var command = new RegisterCommand("testuser", "test@example.com", "password123", "Test User");

        given(userRepository.existsByUsername("testuser")).willReturn(false);
        given(userRepository.existsByEmail("test@example.com")).willReturn(true);

        assertThatThrownBy(() -> registerUseCase.execute(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email already in use");
    }
}
