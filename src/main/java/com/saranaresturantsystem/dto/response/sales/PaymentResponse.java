package com.saranaresturantsystem.dto.response.sales;

import com.saranaresturantsystem.dto.response.common.BaseEntityResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PaymentResponse extends BaseEntityResponse {
    private Long id;
    private String paymentNo;
    private String paymentMethod;
    private Long bank;
    private String bankName;
    private BigDecimal amount;
    private String transactionNo;
    private String status;
    private String paymentDate;
    private Long saleId;
    private String saleNo;
    private Long userId;
    private String userName;
}
