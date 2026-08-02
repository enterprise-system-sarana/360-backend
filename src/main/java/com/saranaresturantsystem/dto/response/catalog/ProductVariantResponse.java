package com.saranaresturantsystem.dto.response.catalog;

import java.math.BigDecimal;
import java.util.List;

public record ProductVariantResponse(
        Long id ,
        String code ,
        BigDecimal costPrice ,
        BigDecimal sellingPrice ,
        String imageUrl,
        String status,
        Long productId,
        String categoryName,
        String brandName,
        String modelName,
        List<VariantValueResponse> variantValues) {
}
