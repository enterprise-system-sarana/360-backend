package com.saranaresturantsystem.dto.response.catalog;

import java.util.List;

public record ProductResponse(
        Long id,
        String code,
        String noted,
        String imageUrl,
        String status,
        Integer reorderLevel,
        Long modelId,
        String modelName,
        String brandName,
        String categoryName,
        List<VariantValueResponse> variantValues
) {
}
