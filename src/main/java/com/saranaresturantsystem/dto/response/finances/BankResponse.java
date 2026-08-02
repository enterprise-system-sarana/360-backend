package com.saranaresturantsystem.dto.response.finances;

import com.saranaresturantsystem.enums.StatusType;

public record BankResponse(
        Long id,
        String accountName,
        String accountNumber,
        Number openingBalance,
        Number currentBalance,
        StatusType status
) {
}
