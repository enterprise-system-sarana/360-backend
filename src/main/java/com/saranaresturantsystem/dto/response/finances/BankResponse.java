package com.saranaresturantsystem.dto.response.finances;

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
public class BankResponse extends BaseEntityResponse {
    private Long id;
    private String name;
    private String accountName;
    private String accountNumber;
    private BigDecimal openingBalance;
    private BigDecimal currentBalance;
    private String status;
}
