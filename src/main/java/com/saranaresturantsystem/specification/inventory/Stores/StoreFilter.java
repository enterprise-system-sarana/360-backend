package com.saranaresturantsystem.specification.inventory.Stores;

import com.saranaresturantsystem.specification.common.StatusFilter;

public record StoreFilter(
        String name,
        String code,
        String email,
        String phone,
        String city,
        String status
) implements StatusFilter {
}
