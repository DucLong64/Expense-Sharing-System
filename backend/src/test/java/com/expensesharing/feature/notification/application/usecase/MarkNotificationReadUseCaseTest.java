package com.expensesharing.feature.notification.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.notification.domain.model.Notification;
import com.expensesharing.feature.notification.domain.model.NotificationType;
import com.expensesharing.feature.notification.domain.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MarkNotificationReadUseCaseTest {

    @Mock NotificationRepository notificationRepository;

    @InjectMocks MarkNotificationReadUseCase markNotificationReadUseCase;

    @Test
    void markAsRead_success() {
        UUID notificationId = UUID.randomUUID();
        Notification notification = Notification.builder()
                .id(notificationId)
                .houseId(TestDataFactory.HOUSE_ID)
                .recipientUserId(TestDataFactory.USER_ID)
                .actorUserId(TestDataFactory.OTHER_USER_ID)
                .type(NotificationType.EXPENSE_CREATED)
                .message("Có khoản chi mới")
                .createdAt(Instant.now())
                .build();

        given(notificationRepository.findByIdAndRecipientUserId(notificationId, TestDataFactory.USER_ID))
                .willReturn(Optional.of(notification));

        Notification result = markNotificationReadUseCase.execute(notificationId, TestDataFactory.USER_ID);

        assertThat(result.isRead()).isTrue();
        then(notificationRepository).should().save(notification);
    }

    @Test
    void markAsRead_notFound_throwsException() {
        UUID notificationId = UUID.randomUUID();
        given(notificationRepository.findByIdAndRecipientUserId(notificationId, TestDataFactory.USER_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> markNotificationReadUseCase.execute(notificationId, TestDataFactory.USER_ID))
                .isInstanceOf(NotFoundException.class);
    }
}
