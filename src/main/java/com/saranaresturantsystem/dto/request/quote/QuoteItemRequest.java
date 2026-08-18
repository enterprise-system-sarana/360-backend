package com.saranaresturantsystem.dto.request.quote;

import java.math.BigDecimal;

public record QuoteItemRequest(

        Long productId,
        BigDecimal price,
        BigDecimal qty,
        BigDecimal discount_item ,
        BigDecimal subtotal


) {
}
