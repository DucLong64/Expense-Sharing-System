package com.expensesharing.feature.expense.application.service;

import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.feature.expense.application.dto.ParticipantShare;
import com.expensesharing.feature.expense.domain.model.ExpenseParticipant;
import com.expensesharing.feature.expense.domain.model.SplitType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SplitCalculator {

    private static final int SCALE = 2;

    public List<ExpenseParticipant> calculate(UUID expenseId, BigDecimal totalAmount,
                                              SplitType splitType, List<ParticipantShare> shares) {
        return switch (splitType) {
            case EQUAL -> calculateEqual(expenseId, totalAmount, shares);
            case FIXED -> calculateFixed(expenseId, totalAmount, shares);
            case PERCENTAGE -> calculatePercentage(expenseId, totalAmount, shares);
        };
    }

    private List<ExpenseParticipant> calculateEqual(UUID expenseId, BigDecimal totalAmount,
                                                    List<ParticipantShare> shares) {
        if (shares == null || shares.isEmpty()) {
            throw new BusinessException("EMPTY_PARTICIPANTS", "Participants list must not be empty.");
        }

        int count = shares.size();
        BigDecimal baseShare = totalAmount.divide(BigDecimal.valueOf(count), SCALE, RoundingMode.DOWN);
        BigDecimal remainder = totalAmount.subtract(baseShare.multiply(BigDecimal.valueOf(count)));

        List<ExpenseParticipant> result = new ArrayList<>();
        for (int i = 0; i < shares.size(); i++) {
            BigDecimal share = (i == 0) ? baseShare.add(remainder) : baseShare;
            result.add(buildParticipant(expenseId, shares.get(i).userId(), share, null));
        }
        return result;
    }

    private List<ExpenseParticipant> calculateFixed(UUID expenseId, BigDecimal totalAmount,
                                                    List<ParticipantShare> shares) {
        BigDecimal sum = shares.stream()
                .map(ParticipantShare::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sum.compareTo(totalAmount) != 0) {
            throw new BusinessException("INVALID_SPLIT_AMOUNT",
                    "Sum of fixed amounts (" + sum + ") must equal total expense amount (" + totalAmount + ").");
        }

        return shares.stream()
                .map(s -> buildParticipant(expenseId, s.userId(), s.amount().setScale(SCALE, RoundingMode.HALF_UP), null))
                .toList();
    }

    private List<ExpenseParticipant> calculatePercentage(UUID expenseId, BigDecimal totalAmount,
                                                         List<ParticipantShare> shares) {
        BigDecimal totalPercentage = shares.stream()
                .map(ParticipantShare::percentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPercentage.compareTo(BigDecimal.valueOf(100)) != 0) {
            throw new BusinessException("INVALID_SPLIT_PERCENTAGE",
                    "Sum of percentages (" + totalPercentage + ") must equal 100.");
        }

        List<ExpenseParticipant> result = new ArrayList<>();
        BigDecimal allocated = BigDecimal.ZERO;

        for (int i = 0; i < shares.size(); i++) {
            ParticipantShare share = shares.get(i);
            BigDecimal shareAmount;

            if (i == shares.size() - 1) {
                shareAmount = totalAmount.subtract(allocated).setScale(SCALE, RoundingMode.HALF_UP);
            } else {
                shareAmount = totalAmount.multiply(share.percentage())
                        .divide(BigDecimal.valueOf(100), SCALE, RoundingMode.DOWN);
                allocated = allocated.add(shareAmount);
            }

            result.add(buildParticipant(expenseId, share.userId(), shareAmount,
                    share.percentage().setScale(SCALE, RoundingMode.HALF_UP)));
        }
        return result;
    }

    private ExpenseParticipant buildParticipant(UUID expenseId, UUID userId,
                                                BigDecimal shareAmount, BigDecimal sharePercentage) {
        return ExpenseParticipant.builder()
                .id(UUID.randomUUID())
                .expenseId(expenseId)
                .userId(userId)
                .shareAmount(shareAmount)
                .sharePercentage(sharePercentage)
                .build();
    }
}
