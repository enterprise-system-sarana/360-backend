package com.saranaresturantsystem.dto.response.inventory;

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
public class StoreResponse extends BaseEntityResponse {
    private Long id;
    private String name;
    private String code;
    private String logo;
    private String email;
    private String phone;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String receiptHeader;
    private String receiptFooter;
    private String status;
}
