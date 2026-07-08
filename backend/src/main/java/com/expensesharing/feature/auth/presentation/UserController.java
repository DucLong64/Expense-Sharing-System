package com.expensesharing.feature.auth.presentation;

import com.expensesharing.common.response.ApiResponse;
import com.expensesharing.feature.auth.application.dto.ChangePasswordCommand;
import com.expensesharing.feature.auth.application.usecase.ChangePasswordUseCase;
import com.expensesharing.feature.auth.application.usecase.GetCurrentUserUseCase;
import com.expensesharing.feature.auth.presentation.request.ChangePasswordRequest;
import com.expensesharing.feature.auth.presentation.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal UUID userId) {
        var user = getCurrentUserUseCase.execute(userId);
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(user)));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        changePasswordUseCase.execute(
                new ChangePasswordCommand(userId, request.currentPassword(), request.newPassword()));
        return ResponseEntity.ok(ApiResponse.ok(null, "Password changed successfully."));
    }
}
