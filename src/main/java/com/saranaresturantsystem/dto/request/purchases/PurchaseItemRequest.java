package com.saranaresturantsystem.dto.request.purchases;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class PurchaseItemRequest {
    private Long productId;
    private BigDecimal quantity;
    private BigDecimal cost;
    private BigDecimal subtotal;
    private List<String> serialNumbers; // List of serial numbers / barcodes for this item
}