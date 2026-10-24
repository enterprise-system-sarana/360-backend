package com.saranaresturantsystem.specification.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.saranaresturantsystem.specification.common.StatusFilter;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentFilter(
        String status,
        Long saleId,
        String paymentNo,
        Long userId,
        Long bankId,
        Long  purchaseId
) implements StatusFilter {
}
