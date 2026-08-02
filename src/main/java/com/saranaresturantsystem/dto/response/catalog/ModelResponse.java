package com.saranaresturantsystem.dto.response.catalog;

public record ModelResponse(
        Long id,
        String name,
        Long brandId,
        String brandName,
        Long categoryId,
        String categoryName,
        String status
)
{
}
