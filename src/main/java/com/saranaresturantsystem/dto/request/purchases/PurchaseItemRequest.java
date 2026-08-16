package com.saranaresturantsystem.dto.request.purchases;


import java.math.BigDecimal;
import java.util.List;


public record PurchaseItemRequest(
        Long productId ,
        BigDecimal quantity ,
        BigDecimal cost ,
        BigDecimal price ,
        List<String> serialNumbers
)
{}