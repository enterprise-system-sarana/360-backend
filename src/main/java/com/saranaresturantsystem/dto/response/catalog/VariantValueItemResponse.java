package com.saranaresturantsystem.dto.response.catalog;

public record VariantValueItemResponse(
        Long id,
        String code,
        String name,
        String status
) {
}
