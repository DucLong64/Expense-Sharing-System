package com.expensesharing.feature.auth.application.usecase;

import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.feature.auth.application.dto.AuthResult;
import com.expensesharing.feature.auth.application.dto.RegisterCommand;
import com.expensesharing.feature.auth.application.port.RefreshTokenRepository;
import com.expensesharing.feature.auth.application.port.TokenService;
import com.expensesharing.feature.auth.domain.model.User;
import com.expensesharing.feature.auth.domain.repository.UserRepository;
import com.expensesharing.feature.auth.domain.service.PasswordEncoder;
import com.expensesharing.feature.auth.domain.service.UsernamePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public AuthResult execute(RegisterCommand command) {
        String username = UsernamePolicy.normalize(command.username());
        UsernamePolicy.validate(username);

        String email = command.email().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new BusinessException("USERNAME_ALREADY_EXISTS", "Username already in use.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("EMAIL_ALREADY_EXISTS", "Email already in use.");
        }

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(command.password()))
                .fullName(command.fullName().trim())
                .createdAt(Instant.now())
                .build();

        userRepository.save(user);

        String accessToken = tokenService.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = tokenService.generateRefreshToken(user.getId());
        refreshTokenRepository.save(user.getId(), refreshToken);

        return new AuthResult(accessToken, refreshToken);
    }
}
