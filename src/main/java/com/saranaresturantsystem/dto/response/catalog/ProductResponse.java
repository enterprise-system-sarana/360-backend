package com.saranaresturantsystem.dto.response.catalog;

public record ProductResponse(
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
