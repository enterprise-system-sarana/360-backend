package com.saranaresturantsystem.dto.response.purchases;

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
public class ExpenseResponse extends BaseEntityResponse {
    private Long id;
    private String reference;
    private BigDecimal amount;
    private String note;
    private Long storeId;
    private String storeName;
    private Long bankId;
    private String bankName;
    private Long expenseTypeId;
    private String expenseTypeName;
    private String status;
}