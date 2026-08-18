package com.saranaresturantsystem.dto.response.quote;

import java.math.BigDecimal;

public record QuoteItemResponse(

        Long productId,
        BigDecimal price,
        BigDecimal qty,
        BigDecimal discount_item ,
        BigDecimal subtotal

) {
}
