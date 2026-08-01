package com.saranaresturantsystem.dto.response;

public record CategoryResponse(
        Long id ,
        String name,
        String code ,
        String imageUrl ,
        String status
) {
}
