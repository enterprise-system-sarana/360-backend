package com.saranaresturantsystem.dto.request.quote;

import com.saranaresturantsystem.enums.PaymentStatus;
import com.saranaresturantsystem.enums.StatusType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record QuoteRequest(

        LocalDateTime date,
        String reference,
        String no,
        Long customerId,
        BigDecimal grandTotal,
        BigDecimal discount,
        StatusType status,
        PaymentStatus status_payment,
        BigDecimal paid_amount,
        BigDecimal return_amount,

        String noted,
        List<QuoteItemRequest> items
) {
}
