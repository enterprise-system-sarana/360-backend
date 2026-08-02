package com.saranaresturantsystem.dto.request.finances;

import com.saranaresturantsystem.enums.StatusType;
import jakarta.validation.constraints.NotNull;

public record BankRequest(
        @NotNull(message = "name is required")
        String name,
        @NotNull(message = "account name is required")
        String accountName,
        @NotNull(message = "account number is required")
        String accountNumber,
        @NotNull(message = "opening balance is required")
        Number openingBalance,
        Number currentBalance,
        StatusType status
) {
}
