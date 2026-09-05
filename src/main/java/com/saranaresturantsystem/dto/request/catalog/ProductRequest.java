package com.saranaresturantsystem.dto.request.catalog;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ProductRequest(
        String code,
        String name ,
        BigDecimal costPrice ,
        BigDecimal salePrice ,
        String noted,
        String imageUrl,
        String status,
        Integer reorderLevel,
        @NotNull(message = "modelId is required")
        Long modelId,
        List<Long> variantValueIds
) {
}
