package com.expensesharing.feature.auth.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.auth.application.dto.ChangePasswordCommand;
import com.expensesharing.feature.auth.domain.model.User;
import com.expensesharing.feature.auth.domain.repository.UserRepository;
import com.expensesharing.feature.auth.domain.service.PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ChangePasswordUseCaseTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks ChangePasswordUseCase changePasswordUseCase;

    private final User user = TestDataFactory.aUser();

    @Test
    void changePassword_success() {
        var command = new ChangePasswordCommand(TestDataFactory.USER_ID, "old_password", "new_password123");

        given(userRepository.findById(TestDataFactory.USER_ID)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("old_password", user.getPassword())).willReturn(true);
        given(passwordEncoder.matches("new_password123", user.getPassword())).willReturn(false);
        given(passwordEncoder.encode("new_password123")).willReturn("encoded_new_password");

        changePasswordUseCase.execute(command);

        then(userRepository).should().save(user);
    }

    @Test
    void changePassword_userNotFound_throwsException() {
        UUID unknownId = UUID.randomUUID();
        var command = new ChangePasswordCommand(unknownId, "old_password", "new_password123");

        given(userRepository.findById(unknownId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> changePasswordUseCase.execute(command))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void changePassword_wrongCurrentPassword_throwsException() {
        var command = new ChangePasswordCommand(TestDataFactory.USER_ID, "wrong_password", "new_password123");

        given(userRepository.findById(TestDataFactory.USER_ID)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrong_password", user.getPassword())).willReturn(false);

        assertThatThrownBy(() -> changePasswordUseCase.execute(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Current password is incorrect");
    }

    @Test
    void changePassword_sameAsCurrent_throwsException() {
        var command = new ChangePasswordCommand(TestDataFactory.USER_ID, "old_password", "old_password");

        given(userRepository.findById(TestDataFactory.USER_ID)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("old_password", user.getPassword())).willReturn(true);

        assertThatThrownBy(() -> changePasswordUseCase.execute(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("must be different");
    }
}
