package com.saranaresturantsystem.dto.response.catalog;

public record ProductResponse(
        Long id,
        String code,
        String noted,
        String imageUrl,
        String status,
        Long modelId,
        String modelName
) {
}
