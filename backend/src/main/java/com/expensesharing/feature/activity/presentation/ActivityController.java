package com.expensesharing.feature.activity.presentation;

import com.expensesharing.common.response.ApiResponse;
import com.expensesharing.common.support.UsernameEnricher;
import com.expensesharing.feature.activity.application.usecase.GetHouseActivitiesUseCase;
import com.expensesharing.feature.activity.application.usecase.GetMyActivitiesUseCase;
import com.expensesharing.feature.activity.domain.model.ActivityType;
import com.expensesharing.feature.activity.presentation.response.ActivityLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ActivityController {

    private final GetHouseActivitiesUseCase getHouseActivitiesUseCase;
    private final GetMyActivitiesUseCase getMyActivitiesUseCase;
    private final UsernameEnricher usernameEnricher;

    @GetMapping("/api/v1/houses/{houseId}/activities")
    public ResponseEntity<ApiResponse<List<ActivityLogResponse>>> getHouseActivities(
            @PathVariable UUID houseId,
            @RequestParam(required = false) ActivityType activityType,
            @AuthenticationPrincipal UUID userId) {
        List<ActivityLogResponse> activities = usernameEnricher.toActivityResponses(
                getHouseActivitiesUseCase.execute(houseId, userId, activityType));
        return ResponseEntity.ok(ApiResponse.ok(activities));
    }

    @GetMapping("/api/v1/users/me/activities")
    public ResponseEntity<ApiResponse<List<ActivityLogResponse>>> getMyActivities(
            @RequestParam(required = false) UUID houseId,
            @RequestParam(required = false) ActivityType activityType,
            @AuthenticationPrincipal UUID userId) {
        List<ActivityLogResponse> activities = usernameEnricher.toActivityResponses(
                getMyActivitiesUseCase.execute(userId, houseId, activityType));
        return ResponseEntity.ok(ApiResponse.ok(activities));
    }
}
