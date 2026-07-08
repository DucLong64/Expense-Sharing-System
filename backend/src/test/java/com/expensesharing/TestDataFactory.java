package com.expensesharing;

import com.expensesharing.feature.auth.domain.model.User;
import com.expensesharing.feature.expense.domain.model.Expense;
import com.expensesharing.feature.expense.domain.model.ExpenseParticipant;
import com.expensesharing.feature.expense.domain.model.SplitType;
import com.expensesharing.feature.house.domain.model.House;
import com.expensesharing.feature.house.domain.model.HouseMember;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.settlement.domain.model.Settlement;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class TestDataFactory {

    public static final UUID USER_ID = UUID.randomUUID();
    public static final UUID OTHER_USER_ID = UUID.randomUUID();
    public static final UUID HOUSE_ID = UUID.randomUUID();
    public static final UUID EXPENSE_ID = UUID.randomUUID();
    public static final UUID SETTLEMENT_ID = UUID.randomUUID();

    public static User aUser() {
        return User.builder()
                .id(USER_ID)
                .username("testuser")
                .email("test@example.com")
                .password("encoded_password")
                .fullName("Test User")
                .createdAt(Instant.now())
                .build();
    }

    public static User aUser(UUID id, String email) {
        return User.builder()
                .id(id)
                .username("user_" + id.toString().substring(0, 8))
                .email(email)
                .password("encoded_password")
                .fullName("Test User")
                .createdAt(Instant.now())
                .build();
    }

    public static House aHouse() {
        return House.builder()
                .id(HOUSE_ID)
                .name("Test House")
                .description("Test Description")
                .createdBy(USER_ID)
                .createdAt(Instant.now())
                .build();
    }

    public static HouseMember aMember(UUID houseId, UUID userId, HouseRole role) {
        return HouseMember.builder()
                .id(UUID.randomUUID())
                .houseId(houseId)
                .userId(userId)
                .role(role)
                .joinedAt(Instant.now())
                .build();
    }

    public static Expense anExpense(UUID houseId, UUID paidBy, UUID createdBy, BigDecimal amount) {
        return Expense.builder()
                .id(EXPENSE_ID)
                .houseId(houseId)
                .title("Test Expense")
                .description("Test Description")
                .amount(amount)
                .paidBy(paidBy)
                .splitType(SplitType.EQUAL)
                .expenseDate(LocalDate.now())
                .createdBy(createdBy)
                .createdAt(Instant.now())
                .build();
    }

    public static ExpenseParticipant aParticipant(UUID expenseId, UUID userId, BigDecimal shareAmount) {
        return ExpenseParticipant.builder()
                .id(UUID.randomUUID())
                .expenseId(expenseId)
                .userId(userId)
                .shareAmount(shareAmount)
                .build();
    }

    public static Settlement aSettlement(UUID houseId, UUID fromUserId, UUID toUserId, BigDecimal amount) {
        return Settlement.builder()
                .id(SETTLEMENT_ID)
                .houseId(houseId)
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .amount(amount)
                .settledAt(Instant.now())
                .createdBy(fromUserId)
                .build();
    }
}
