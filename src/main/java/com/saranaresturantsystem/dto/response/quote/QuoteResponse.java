package com.saranaresturantsystem.dto.response.quote;

import com.saranaresturantsystem.dto.response.common.BaseEntityResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class QuoteResponse extends BaseEntityResponse {
    private Long id;
    private LocalDate date;
    private String reference;
    private String no;
    private Long customerId;
    private BigDecimal grandTotal;
    private BigDecimal discount;
    private String status;
    private String statusPayment;
    private BigDecimal paidAmount;
    private BigDecimal returnAmount;
    private Long productId;
    private String noted;
    private List<QuoteItemResponse> items;
}
