package com.saranaresturantsystem.dto.request.purchases;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PurchaseRequest {
    private String referenceNo;
    private Long supplierId;
    private Long storeId;
    private LocalDate purchaseDate;
    private BigDecimal total;
    private BigDecimal discount;
    private BigDecimal grandTotal;
    private String status;
    private String note;
    private List<PurchaseItemRequest> items;
}