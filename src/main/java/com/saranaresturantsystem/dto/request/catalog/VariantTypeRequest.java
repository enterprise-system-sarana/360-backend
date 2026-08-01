package com.saranaresturantsystem.dto.request.catalog;

import com.saranaresturantsystem.enums.StatusType;

public record VariantTypeRequest(
        String code,
        String name,
        StatusType status
) {
}
