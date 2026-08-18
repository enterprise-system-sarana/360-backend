package com.saranaresturantsystem.reports.dto.sales;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SaleItemReportResponse(
        Long saleId,
        String saleReference,
        LocalDateTime saleDate,
        Long storeId,
        Long productId,
        String productName,
        BigDecimal quantity,
        BigDecimal price,
        BigDecimal itemDiscount,
        BigDecimal subTotal,
        List<String> serialNumbers
) {}