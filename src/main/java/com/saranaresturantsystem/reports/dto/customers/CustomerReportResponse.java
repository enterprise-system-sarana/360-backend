package com.saranaresturantsystem.reports.dto.customers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerReportResponse {
    private Long id;
    private String name;
    private String code;
    private String phone;
    private String email;
    private String note;
    private String status;
}