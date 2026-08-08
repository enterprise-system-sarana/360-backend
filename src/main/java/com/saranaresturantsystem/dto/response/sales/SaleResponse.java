package com.saranaresturantsystem.dto.response.sales;

import java.util.List;

public record SaleResponse(
        Long id,
        String date,
        String reference,
        String no,
        Long storeId,
        Long customerId,
        Double grandTotal,
        Double discount,
        String status,
        String paymentStatus,
        Double paidAmount,
        Double returnAmount,
        String noted,
        List<SaleItemResponse> items
) {
}
