package com.saranaresturantsystem.specification.payment;

import com.saranaresturantsystem.specification.common.StatusFilter;

public record PaymentFilter(
        String status,
        Long saleId,
        String paymentNo,
        Long userId,
        Long bankId
) implements StatusFilter {
}
