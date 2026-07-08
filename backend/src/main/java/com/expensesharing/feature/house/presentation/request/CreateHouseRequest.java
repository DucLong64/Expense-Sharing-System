package com.expensesharing.feature.house.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateHouseRequest(

        @NotBlank(message = "House name is required")
        @Size(max = 100, message = "House name must not exceed 100 characters")
        String name,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description
) {}
