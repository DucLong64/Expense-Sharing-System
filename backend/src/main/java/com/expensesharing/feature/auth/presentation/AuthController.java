package com.expensesharing.feature.auth.presentation;

import com.expensesharing.common.response.ApiResponse;
import com.expensesharing.feature.auth.application.dto.AuthResult;
import com.expensesharing.feature.auth.application.dto.LoginCommand;
import com.expensesharing.feature.auth.application.dto.RegisterCommand;
import com.expensesharing.feature.auth.application.usecase.LoginUseCase;
import com.expensesharing.feature.auth.application.usecase.LogoutUseCase;
import com.expensesharing.feature.auth.application.usecase.RefreshTokenUseCase;
import com.expensesharing.feature.auth.application.usecase.RegisterUseCase;
import com.expensesharing.feature.auth.presentation.request.LoginRequest;
import com.expensesharing.feature.auth.presentation.request.RefreshTokenRequest;
import com.expensesharing.feature.auth.presentation.request.RegisterRequest;
import com.expensesharing.feature.auth.presentation.response.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResult result = registerUseCase.execute(
                new RegisterCommand(request.username(), request.email(), request.password(), request.fullName()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(toResponse(result), "Registration successful."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = loginUseCase.execute(
                new LoginCommand(request.username(), request.password()));
        return ResponseEntity.ok(ApiResponse.ok(toResponse(result)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResult result = refreshTokenUseCase.execute(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.ok(toResponse(result)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        logoutUseCase.execute(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.ok(null, "Logged out successfully."));
    }

    private AuthResponse toResponse(AuthResult result) {
        return new AuthResponse(result.accessToken(), result.refreshToken());
    }
}
