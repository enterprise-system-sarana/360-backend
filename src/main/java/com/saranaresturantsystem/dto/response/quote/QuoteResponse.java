package com.saranaresturantsystem.dto.response.quote;

import com.saranaresturantsystem.enums.PaymentStatus;
import com.saranaresturantsystem.enums.StatusType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record QuoteResponse(

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
        Long productId,
        String noted,
        List<QuoteItemResponse> items
) {

}
