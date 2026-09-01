package com.saranaresturantsystem.dto.response.purchases;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PurchaseResponse(
        Long id,
        String referenceNo,
        Long supplierId,
        String supplierName ,
        Long storeId,
        String storeName,
        Long bankId ,
        String bankName,
        LocalDate purchaseDate,
        BigDecimal total,
        BigDecimal discount,
        BigDecimal grandTotal,
        BigDecimal paidAmount,
        BigDecimal dueAmount,
        String paymentStatus,

        String status,
        String note,
        List<PurchaseItemResponse> items
) {
}