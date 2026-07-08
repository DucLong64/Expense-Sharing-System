package com.expensesharing.feature.settlement.application.service;

import com.expensesharing.feature.expense.domain.model.Expense;
import com.expensesharing.feature.expense.domain.model.ExpenseParticipant;
import com.expensesharing.feature.expense.domain.repository.ExpenseParticipantRepository;
import com.expensesharing.feature.expense.domain.repository.ExpenseRepository;
import com.expensesharing.feature.settlement.application.dto.UserBalance;
import com.expensesharing.feature.settlement.domain.model.DebtSummary;
import com.expensesharing.feature.settlement.domain.model.Settlement;
import com.expensesharing.feature.settlement.domain.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DebtCalculator {

    private static final int SCALE = 2;
    private static final BigDecimal EPSILON = new BigDecimal("0.01");

    private final ExpenseRepository expenseRepository;
    private final ExpenseParticipantRepository participantRepository;
    private final SettlementRepository settlementRepository;

    public List<DebtSummary> calculate(UUID houseId) {
        Map<UUID, BigDecimal> netBalance = computeNetBalance(houseId);
        return simplifyDebts(netBalance);
    }

    // Tính net balance: dương = được nhận tiền, âm = phải trả tiền
    private Map<UUID, BigDecimal> computeNetBalance(UUID houseId) {
        Map<UUID, BigDecimal> balance = new HashMap<>();

        // Cộng tiền cho người đã trả expense
        for (Expense expense : expenseRepository.findAllByHouseId(houseId)) {
            balance.merge(expense.getPaidBy(), expense.getAmount(), BigDecimal::add);

            // Trừ tiền của từng participant theo share
            for (ExpenseParticipant p : participantRepository.findAllByExpenseId(expense.getId())) {
                balance.merge(p.getUserId(), p.getShareAmount().negate(), BigDecimal::add);
            }
        }

        // Người trả (from) giảm nợ; người nhận (to) giảm khoản được nhận
        for (Settlement s : settlementRepository.findAllByHouseId(houseId)) {
            balance.merge(s.getFromUserId(), s.getAmount(), BigDecimal::add);
            balance.merge(s.getToUserId(), s.getAmount().negate(), BigDecimal::add);
        }

        return balance;
    }

    // Greedy algorithm: tối ưu số giao dịch thanh toán
    private List<DebtSummary> simplifyDebts(Map<UUID, BigDecimal> netBalance) {
        // Tách thành 2 danh sách: creditors (balance > 0) và debtors (balance < 0)
        List<UserBalance> creditors = new ArrayList<>();
        List<UserBalance> debtors = new ArrayList<>();

        for (Map.Entry<UUID, BigDecimal> entry : netBalance.entrySet()) {
            BigDecimal bal = entry.getValue().setScale(SCALE, RoundingMode.HALF_UP);
            if (bal.compareTo(EPSILON) > 0) {
                creditors.add(new UserBalance(entry.getKey(), bal));
            } else if (bal.compareTo(EPSILON.negate()) < 0) {
                debtors.add(new UserBalance(entry.getKey(), bal.abs()));
            }
        }

        // Sort descending để match lớn nhất trước
        creditors.sort((a, b) -> b.balance().compareTo(a.balance()));
        debtors.sort((a, b) -> b.balance().compareTo(a.balance()));

        List<DebtSummary> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < debtors.size() && j < creditors.size()) {
            UserBalance debtor = debtors.get(i);
            UserBalance creditor = creditors.get(j);

            BigDecimal transferAmount = debtor.balance().min(creditor.balance())
                    .setScale(SCALE, RoundingMode.HALF_UP);

            result.add(new DebtSummary(debtor.userId(), creditor.userId(), transferAmount));

            BigDecimal remainDebtor = debtor.balance().subtract(transferAmount);
            BigDecimal remainCreditor = creditor.balance().subtract(transferAmount);

            debtors.set(i, new UserBalance(debtor.userId(), remainDebtor));
            creditors.set(j, new UserBalance(creditor.userId(), remainCreditor));

            if (remainDebtor.compareTo(EPSILON) < 0) i++;
            if (remainCreditor.compareTo(EPSILON) < 0) j++;
        }

        return result;
    }
}
