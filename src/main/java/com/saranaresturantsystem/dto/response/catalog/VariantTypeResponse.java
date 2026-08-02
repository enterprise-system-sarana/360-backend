package com.saranaresturantsystem.dto.response.catalog;

public record VariantTypeResponse(
        Long id,
        String code,
        String name,
        String status
) {
}
