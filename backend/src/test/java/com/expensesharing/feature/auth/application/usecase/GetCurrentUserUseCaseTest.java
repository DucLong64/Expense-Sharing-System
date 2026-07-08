package com.expensesharing.feature.auth.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.auth.domain.model.User;
import com.expensesharing.feature.auth.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetCurrentUserUseCaseTest {

    @Mock UserRepository userRepository;

    @InjectMocks GetCurrentUserUseCase getCurrentUserUseCase;

    private final User user = TestDataFactory.aUser();

    @Test
    void getCurrentUser_success() {
        given(userRepository.findById(TestDataFactory.USER_ID)).willReturn(Optional.of(user));

        User result = getCurrentUserUseCase.execute(TestDataFactory.USER_ID);

        assertThat(result.getId()).isEqualTo(TestDataFactory.USER_ID);
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    void getCurrentUser_notFound_throwsException() {
        UUID unknownId = UUID.randomUUID();
        given(userRepository.findById(unknownId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> getCurrentUserUseCase.execute(unknownId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
