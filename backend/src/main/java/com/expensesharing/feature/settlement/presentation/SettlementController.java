package com.expensesharing.feature.settlement.presentation;

import com.expensesharing.common.response.ApiResponse;
import com.expensesharing.common.support.UsernameEnricher;
import com.expensesharing.feature.settlement.application.dto.ConfirmDebtReceivedCommand;
import com.expensesharing.feature.settlement.application.dto.SettleDebtCommand;
import com.expensesharing.feature.settlement.application.usecase.ConfirmDebtReceivedUseCase;
import com.expensesharing.feature.settlement.application.usecase.GetDebtSummaryUseCase;
import com.expensesharing.feature.settlement.application.usecase.GetSettlementHistoryUseCase;
import com.expensesharing.feature.settlement.application.usecase.SettleDebtUseCase;
import com.expensesharing.feature.settlement.presentation.request.ConfirmDebtReceivedRequest;
import com.expensesharing.feature.settlement.presentation.request.SettleDebtRequest;
import com.expensesharing.feature.settlement.presentation.response.DebtSummaryResponse;
import com.expensesharing.feature.settlement.presentation.response.SettlementResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/houses/{houseId}")
@RequiredArgsConstructor
public class SettlementController {

    private final GetDebtSummaryUseCase getDebtSummaryUseCase;
    private final SettleDebtUseCase settleDebtUseCase;
    private final ConfirmDebtReceivedUseCase confirmDebtReceivedUseCase;
    private final GetSettlementHistoryUseCase getSettlementHistoryUseCase;
    private final UsernameEnricher usernameEnricher;

    @GetMapping("/debts")
    public ResponseEntity<ApiResponse<List<DebtSummaryResponse>>> getDebts(
            @PathVariable UUID houseId,
            @AuthenticationPrincipal UUID userId) {
        List<DebtSummaryResponse> debts = usernameEnricher.toDebtResponses(
                getDebtSummaryUseCase.execute(houseId, userId));
        return ResponseEntity.ok(ApiResponse.ok(debts));
    }

    @PostMapping("/settlements")
    public ResponseEntity<ApiResponse<SettlementResponse>> settle(
            @PathVariable UUID houseId,
            @Valid @RequestBody SettleDebtRequest request,
            @AuthenticationPrincipal UUID userId) {
        var settlement = settleDebtUseCase.execute(
                new SettleDebtCommand(houseId, userId, request.toUserId(), request.amount(), request.note()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(usernameEnricher.toSettlementResponse(settlement), "Settlement recorded successfully."));
    }

    @PostMapping("/settlements/confirm-received")
    public ResponseEntity<ApiResponse<SettlementResponse>> confirmReceived(
            @PathVariable UUID houseId,
            @Valid @RequestBody ConfirmDebtReceivedRequest request,
            @AuthenticationPrincipal UUID userId) {
        var settlement = confirmDebtReceivedUseCase.execute(
                new ConfirmDebtReceivedCommand(
                        houseId, userId, request.fromUserId(), request.amount(), request.note()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(
                        usernameEnricher.toSettlementResponse(settlement),
                        "Debt received confirmed successfully."));
    }

    @GetMapping("/settlements")
    public ResponseEntity<ApiResponse<List<SettlementResponse>>> getHistory(
            @PathVariable UUID houseId,
            @AuthenticationPrincipal UUID userId) {
        List<SettlementResponse> history = usernameEnricher.toSettlementResponses(
                getSettlementHistoryUseCase.execute(houseId, userId));
        return ResponseEntity.ok(ApiResponse.ok(history));
    }
}
