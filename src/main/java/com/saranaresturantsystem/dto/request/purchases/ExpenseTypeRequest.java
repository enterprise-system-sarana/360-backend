package com.saranaresturantsystem.dto.request.purchases;

import com.saranaresturantsystem.enums.StatusType;
import jakarta.validation.constraints.NotBlank;

public record ExpenseTypeRequest(
        @NotBlank(message = "Code is required")
        String code,
        @NotBlank(message = "Name is required")
        String name,
        String description,
        StatusType status
) {}