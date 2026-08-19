package com.saranaresturantsystem.specification.payment;

public record PaymentFilter(
        Long saleId ,
        String paymentNo ,
        String status ,
        Long userId,
        Long bank
) {
}
