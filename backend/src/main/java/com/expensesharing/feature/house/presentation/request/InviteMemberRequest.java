package com.expensesharing.feature.house.presentation.request;

import com.expensesharing.feature.house.domain.model.HouseRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InviteMemberRequest(

        @NotBlank(message = "Identifier is required")
        String identifier,

        @NotNull(message = "Role is required")
        HouseRole role
) {}
