package com.saranaresturantsystem.specification.quote;

import com.saranaresturantsystem.enums.PaymentStatus;
import com.saranaresturantsystem.enums.StatusType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record QuoteFilter(
        Long id,
        LocalDate date,
        String reference,
        String no,
        Long customerId,
        BigDecimal grandTotal,
        BigDecimal discount,
        StatusType status,
        PaymentStatus status_payment,
        BigDecimal paid_amount,
        BigDecimal return_amount,
        String noted
) {
}
