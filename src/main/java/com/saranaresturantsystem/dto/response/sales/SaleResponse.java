package com.saranaresturantsystem.dto.response.sales;

import com.saranaresturantsystem.dto.response.common.BaseEntityResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SaleResponse extends BaseEntityResponse {
    private Long id;
    private String date;
    private String reference;
    private String no;
    private Long storeId;
    private String storeName;
    private  Long bankId ;
    private  Long bankName ;
    private Long customerId;
    private  Long customerName ;
    private Double grandTotal;
    private Double discount;
    private String status;
    private String paymentStatus;
    private Double paidAmount;
    private Double returnAmount;
    private String noted;
    private List<SaleItemResponse> items;
}
