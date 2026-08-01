package com.saranaresturantsystem.dto.request.purchases;

import com.saranaresturantsystem.enums.StatusType;

public record SupplierRequest(
        String code ,
        String name,
        String phone ,
        String email ,String address ,
        String city ,
        String country ,
        String note ,
        StatusType status
) {
}
