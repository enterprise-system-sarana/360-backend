package com.saranaresturantsystem.dto.response.catalog;

public record BrandResponse(
        Long id,
        String name,
        String imageUrl,
        String status
) {
}
