package com.saranaresturantsystem.specification.inventory.Stores;

public record StoreFilter(
        String name ,
        String code ,
        String email ,
        String phone ,
        String city,
        String status
) {
}
