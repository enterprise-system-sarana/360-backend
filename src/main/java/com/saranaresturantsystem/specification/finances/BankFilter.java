package com.saranaresturantsystem.specification.finances;

public record BankFilter(
        String name,
        String accountName,
        String accountNumber
) {
}
