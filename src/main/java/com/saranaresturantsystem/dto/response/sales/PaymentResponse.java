package com.saranaresturantsystem.dto.response.sales;

import java.math.BigDecimal;

public record PaymentResponse(
        Long id ,
        String paymentNo ,
        String paymentMethod ,
        Long bank ,
        String bankName ,
        BigDecimal amount ,
        String transactionNo ,
        String status ,
        String paymentDate ,
        Long saleId ,
        String saleNo ,
        Long userId,
        String userName

) {
}
