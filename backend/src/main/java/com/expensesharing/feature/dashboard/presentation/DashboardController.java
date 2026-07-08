package com.expensesharing.feature.dashboard.presentation;

import com.expensesharing.common.response.ApiResponse;
import com.expensesharing.common.support.UsernameEnricher;
import com.expensesharing.feature.dashboard.application.usecase.GetHouseDashboardUseCase;
import com.expensesharing.feature.dashboard.presentation.response.HouseDashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/houses/{houseId}/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final GetHouseDashboardUseCase getHouseDashboardUseCase;
    private final UsernameEnricher usernameEnricher;

    @GetMapping
    public ResponseEntity<ApiResponse<HouseDashboardResponse>> getDashboard(
            @PathVariable UUID houseId,
            @AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(ApiResponse.ok(
                usernameEnricher.toDashboardResponse(getHouseDashboardUseCase.execute(houseId, userId))));
    }
}
