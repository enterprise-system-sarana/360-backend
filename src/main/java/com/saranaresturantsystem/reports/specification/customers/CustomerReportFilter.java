package com.saranaresturantsystem.reports.specification.customers;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerReportFilter {
    private String name;
    private String code;
    private String phone;
    private String email;
    private String status;
}