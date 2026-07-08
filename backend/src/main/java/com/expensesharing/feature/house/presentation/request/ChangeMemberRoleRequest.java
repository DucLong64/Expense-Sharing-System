package com.expensesharing.feature.house.presentation.request;

import com.expensesharing.feature.house.domain.model.HouseRole;
import jakarta.validation.constraints.NotNull;

public record ChangeMemberRoleRequest(

        @NotNull(message = "Role is required")
        HouseRole role
) {}
