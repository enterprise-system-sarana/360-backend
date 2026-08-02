package com.saranaresturantsystem.dto.request.catalog;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ProductVariantRequest(
        @NotNull(message = "code is require")
        String code ,
        BigDecimal costPrice ,
        BigDecimal sellingPrice,
        String imageUrl ,
        String status ,
        Long productId,
        List<Long> variantValueIds
) {
}
