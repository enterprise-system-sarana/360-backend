package com.saranaresturantsystem.dto.response.catalog;

public record CategoryResponse(
        Long id ,
        String name,
        String code ,
        String imageUrl ,
        String status
) {
}
