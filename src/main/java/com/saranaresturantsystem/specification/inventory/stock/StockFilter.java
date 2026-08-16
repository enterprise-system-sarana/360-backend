package com.saranaresturantsystem.specification.inventory.stock;

public record StockFilter(
        Long productId ,
        Long storeId
) {
}
