package com.saranaresturantsystem.dto.request.finances;


import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BankRequest(
        @NotNull(message = "name is required")
        String name,
        @NotNull(message = "account name is required")
        String accountName,
        @NotNull(message = "account number is required")
        String accountNumber,
        @NotNull(message = "opening balance is required")
        BigDecimal openingBalance,
        BigDecimal currentBalance,
        String status
) {
}
