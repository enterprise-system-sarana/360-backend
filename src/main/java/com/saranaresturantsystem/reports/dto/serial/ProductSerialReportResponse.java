package com.saranaresturantsystem.reports.dto.serial;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductSerialReportResponse {
    private Long id;
    private Long productId;
    private String barcode;
    private BigDecimal price;
    private BigDecimal cost;
    private BigDecimal quantity;
    private BigDecimal alertQuantity;
    private Long storeId;
    private Long purchaseId;
    private String status;
    private LocalDateTime createdAt;
}