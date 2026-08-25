package com.saranaresturantsystem.dto.response.purchases;

import com.saranaresturantsystem.dto.response.common.BaseEntityResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ExpenseTypeResponse extends BaseEntityResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String status;
}