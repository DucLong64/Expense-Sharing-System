package com.expensesharing.feature.expense.presentation;

import com.expensesharing.common.response.ApiResponse;
import com.expensesharing.common.support.UsernameEnricher;
import com.expensesharing.feature.expense.application.dto.CreateExpenseCommand;
import com.expensesharing.feature.expense.application.dto.ParticipantShare;
import com.expensesharing.feature.expense.application.dto.UpdateExpenseCommand;
import com.expensesharing.feature.expense.application.usecase.CreateExpenseUseCase;
import com.expensesharing.feature.expense.application.usecase.DeleteExpenseUseCase;
import com.expensesharing.feature.expense.application.usecase.GetExpenseUseCase;
import com.expensesharing.feature.expense.application.usecase.UpdateExpenseUseCase;
import com.expensesharing.feature.expense.presentation.request.CreateExpenseRequest;
import com.expensesharing.feature.expense.presentation.request.UpdateExpenseRequest;
import com.expensesharing.feature.expense.presentation.response.ExpenseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/houses/{houseId}/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final CreateExpenseUseCase createExpenseUseCase;
    private final UpdateExpenseUseCase updateExpenseUseCase;
    private final DeleteExpenseUseCase deleteExpenseUseCase;
    private final GetExpenseUseCase getExpenseUseCase;
    private final UsernameEnricher usernameEnricher;

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> create(
            @PathVariable UUID houseId,
            @Valid @RequestBody CreateExpenseRequest request,
            @AuthenticationPrincipal UUID userId) {
        var result = createExpenseUseCase.execute(new CreateExpenseCommand(
                houseId, userId,
                request.title(), request.description(), request.amount(),
                request.paidBy(), request.splitType(), request.expenseDate(), request.note(),
                toParticipantShares(request)));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(usernameEnricher.toExpenseResponse(result), "Expense created successfully."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getAll(
            @PathVariable UUID houseId,
            @AuthenticationPrincipal UUID userId) {
        List<ExpenseResponse> expenses = usernameEnricher.toExpenseResponses(
                getExpenseUseCase.getAllByHouseId(houseId, userId));
        return ResponseEntity.ok(ApiResponse.ok(expenses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getById(
            @PathVariable UUID houseId,
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {
        var result = getExpenseUseCase.getById(id, houseId, userId);
        return ResponseEntity.ok(ApiResponse.ok(usernameEnricher.toExpenseResponse(result)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> update(
            @PathVariable UUID houseId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateExpenseRequest request,
            @AuthenticationPrincipal UUID userId) {
        var result = updateExpenseUseCase.execute(new UpdateExpenseCommand(
                id, houseId, userId,
                request.title(), request.description(), request.amount(),
                request.splitType(), request.expenseDate(), request.note(),
                toParticipantShares(request)));
        return ResponseEntity.ok(ApiResponse.ok(usernameEnricher.toExpenseResponse(result)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID houseId,
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {
        deleteExpenseUseCase.execute(id, houseId, userId);
        return ResponseEntity.ok(ApiResponse.ok(null, "Expense deleted successfully."));
    }

    private List<ParticipantShare> toParticipantShares(CreateExpenseRequest request) {
        return request.participants().stream()
                .map(p -> new ParticipantShare(p.userId(), p.amount(), p.percentage()))
                .toList();
    }

    private List<ParticipantShare> toParticipantShares(UpdateExpenseRequest request) {
        return request.participants().stream()
                .map(p -> new ParticipantShare(p.userId(), p.amount(), p.percentage()))
                .toList();
    }
}
