package com.saranaresturantsystem.dto.request.quote;

import com.saranaresturantsystem.enums.PaymentStatus;
import com.saranaresturantsystem.enums.StatusType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record QuoteItemRequest(

        Long productId,
        BigDecimal price,
        BigDecimal qty,
        BigDecimal discount_item ,
        BigDecimal subtotal,

        Long product_serial
) {
}
