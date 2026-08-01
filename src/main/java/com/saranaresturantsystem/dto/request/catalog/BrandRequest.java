package com.saranaresturantsystem.dto.request.catalog;

import com.saranaresturantsystem.enums.StatusType;

public record BrandRequest(
        String name,
        String imageUrl,
        StatusType status
) {

}



