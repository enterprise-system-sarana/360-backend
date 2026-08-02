package com.saranaresturantsystem.dto.response.catalog;

public record VariantValueResponse(
        Long id,
        String code,
        String name,
        Long variantTypeId,
        String variantTypeName,
        String status
) {
}