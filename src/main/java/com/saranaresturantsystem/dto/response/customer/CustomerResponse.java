package com.saranaresturantsystem.dto.response.customer;

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
public class CustomerResponse extends BaseEntityResponse {
    private Long id;
    private String code;
    private String name;
    private String phone;
    private String email;
    private String note;
    private String status;
}
