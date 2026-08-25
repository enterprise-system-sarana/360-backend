package com.saranaresturantsystem.dto.request.quote;

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
        String status,
        String paymentStatus,
        BigDecimal paidAmount,
        BigDecimal returnAmount,

        String noted,
        List<QuoteItemRequest> items
) {
}
