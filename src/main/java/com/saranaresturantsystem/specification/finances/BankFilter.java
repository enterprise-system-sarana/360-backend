package com.saranaresturantsystem.specification.finances;

import com.saranaresturantsystem.specification.common.StatusFilter;

public record BankFilter(
        String name,
        String accountName,
        String accountNumber,
        String status
) implements StatusFilter {
}
