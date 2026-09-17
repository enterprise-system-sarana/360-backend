package com.saranaresturantsystem.dto.response.sales;

import com.saranaresturantsystem.dto.response.catalog.ProductSerialResponse;
import java.util.List;

public record SaleItemResponse(
        Long id,
        Long productId,
        String productName,
        Integer qty,
        Double price,
        Double itemDiscount,
        Double subTotal,
        List<Long> productSerialIds,
        List<String> serialNumbers,
        List<ProductSerialResponse> serials
) {
}

