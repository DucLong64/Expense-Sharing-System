package com.expensesharing.feature.notification.presentation;

import com.expensesharing.common.response.ApiResponse;
import com.expensesharing.common.support.UsernameEnricher;
import com.expensesharing.feature.notification.application.usecase.GetMyNotificationsUseCase;
import com.expensesharing.feature.notification.application.usecase.GetUnreadNotificationCountUseCase;
import com.expensesharing.feature.notification.application.usecase.MarkAllNotificationsReadUseCase;
import com.expensesharing.feature.notification.application.usecase.MarkNotificationReadUseCase;
import com.expensesharing.feature.notification.presentation.response.NotificationResponse;
import com.expensesharing.feature.notification.presentation.response.UnreadNotificationCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final GetMyNotificationsUseCase getMyNotificationsUseCase;
    private final GetUnreadNotificationCountUseCase getUnreadNotificationCountUseCase;
    private final MarkNotificationReadUseCase markNotificationReadUseCase;
    private final MarkAllNotificationsReadUseCase markAllNotificationsReadUseCase;
    private final UsernameEnricher usernameEnricher;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(required = false) UUID houseId,
            @RequestParam(required = false) Boolean unreadOnly) {
        List<NotificationResponse> notifications = usernameEnricher.toNotificationResponses(
                getMyNotificationsUseCase.execute(userId, houseId, unreadOnly));
        return ResponseEntity.ok(ApiResponse.ok(notifications));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<UnreadNotificationCountResponse>> getUnreadCount(
            @AuthenticationPrincipal UUID userId) {
        long count = getUnreadNotificationCountUseCase.execute(userId);
        return ResponseEntity.ok(ApiResponse.ok(new UnreadNotificationCountResponse(count)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {
        var notification = markNotificationReadUseCase.execute(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(
                usernameEnricher.toNotificationResponse(notification)));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@AuthenticationPrincipal UUID userId) {
        markAllNotificationsReadUseCase.execute(userId);
        return ResponseEntity.ok(ApiResponse.ok(null, "All notifications marked as read."));
    }
}
