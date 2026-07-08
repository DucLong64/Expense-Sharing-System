package com.expensesharing.feature.auth.application.usecase;

import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.auth.application.dto.ChangePasswordCommand;
import com.expensesharing.feature.auth.domain.model.User;
import com.expensesharing.feature.auth.domain.repository.UserRepository;
import com.expensesharing.feature.auth.domain.service.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void execute(ChangePasswordCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "User not found."));

        if (!passwordEncoder.matches(command.currentPassword(), user.getPassword())) {
            throw new BusinessException("INVALID_CURRENT_PASSWORD", "Current password is incorrect.");
        }

        if (passwordEncoder.matches(command.newPassword(), user.getPassword())) {
            throw new BusinessException("SAME_PASSWORD", "New password must be different from current password.");
        }

        user.changePassword(passwordEncoder.encode(command.newPassword()));
        userRepository.save(user);
    }
}
