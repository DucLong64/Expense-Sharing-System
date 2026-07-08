package com.expensesharing.feature.house.application.usecase;

import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.house.application.dto.ChangeMemberRoleCommand;
import com.expensesharing.feature.house.domain.model.HouseMember;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChangeMemberRoleUseCase {

    private final HouseMemberRepository houseMemberRepository;

    @Transactional
    public HouseMember execute(ChangeMemberRoleCommand command) {
        HouseMember requester = houseMemberRepository.findByHouseIdAndUserId(command.houseId(), command.requesterId())
                .orElseThrow(() -> new ForbiddenException("NOT_A_MEMBER", "You are not a member of this house."));

        if (!requester.hasPermission(HouseRole.OWNER)) {
            throw new ForbiddenException("INSUFFICIENT_PERMISSION", "Only OWNER can change member roles.");
        }

        HouseMember target = houseMemberRepository.findByHouseIdAndUserId(command.houseId(), command.targetUserId())
                .orElseThrow(() -> new NotFoundException("MEMBER_NOT_FOUND", "Target member not found."));

        if (target.getRole() == HouseRole.OWNER) {
            throw new BusinessException("CANNOT_CHANGE_OWNER_ROLE", "Cannot change the role of the OWNER.");
        }

        if (command.newRole() == HouseRole.OWNER) {
            throw new BusinessException("CANNOT_ASSIGN_OWNER", "Cannot assign OWNER role via this endpoint.");
        }

        target.changeRole(command.newRole());
        return houseMemberRepository.save(target);
    }
}
