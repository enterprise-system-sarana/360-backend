package com.saranaresturantsystem.reports.dto.serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSerialReportResponse {
    private Long id;
    private Long productId;
    private  String productName ;
    private String barcode;
    private BigDecimal price;
    private BigDecimal cost;
    private BigDecimal quantity;
    private BigDecimal alertQuantity;
    private Long storeId;
    private String storeName ;
    private Long purchaseId;
    private String status;
    private LocalDateTime createdAt;
}