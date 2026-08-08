package com.saranaresturantsystem.dto.response.sales;

import java.util.List;

public record SaleItemResponse(
        Long id,
        Long productId,
        String productName,
        Integer qty,
        Double price,
        Double itemDiscount,
        Double subTotal,
        List<Long> productSerialIds
) {
}
