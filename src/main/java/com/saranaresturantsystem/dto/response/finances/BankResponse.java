package com.saranaresturantsystem.dto.response.finances;


public record BankResponse(
        Long id,
        String accountName,
        String accountNumber,
        Number openingBalance,
        Number currentBalance,
        String status
) {
}
