package com.saranaresturantsystem.specification.catalog.productSerial;

public record ProductSerialFilter(
        String barcode ,
//        Long storeId,
        Long productId
) {
}
