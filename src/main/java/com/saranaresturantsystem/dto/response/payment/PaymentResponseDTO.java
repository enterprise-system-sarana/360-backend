package com.saranaresturantsystem.dto.response.payment;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private Long id;
    private String paymentNo;
    private BigDecimal totalAmount;
    private BigDecimal receivedAmount;
    private BigDecimal returnAmount;
    private BigDecimal dueAmount;
    private String status;
    private LocalDateTime paymentDate;
    private String note;
    private Long saleId;
    private Long purchaseId;
    private Long userId;
    private String username;
    private List<PaymentLineResponseDTO> lines;
}