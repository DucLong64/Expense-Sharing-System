package com.expensesharing.feature.auth.application.usecase;

import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.feature.auth.application.dto.AuthResult;
import com.expensesharing.feature.auth.application.dto.LoginCommand;
import com.expensesharing.feature.auth.application.port.RefreshTokenRepository;
import com.expensesharing.feature.auth.application.port.TokenService;
import com.expensesharing.feature.auth.domain.model.User;
import com.expensesharing.feature.auth.domain.repository.UserRepository;
import com.expensesharing.feature.auth.domain.service.PasswordEncoder;
import com.expensesharing.feature.auth.domain.service.UsernamePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthResult execute(LoginCommand command) {
        String username = UsernamePolicy.normalize(command.username());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("INVALID_CREDENTIALS", "Invalid username or password."));

        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new BusinessException("INVALID_CREDENTIALS", "Invalid username or password.");
        }

        String accessToken = tokenService.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = tokenService.generateRefreshToken(user.getId());
        refreshTokenRepository.save(user.getId(), refreshToken);

        return new AuthResult(accessToken, refreshToken);
    }
}
