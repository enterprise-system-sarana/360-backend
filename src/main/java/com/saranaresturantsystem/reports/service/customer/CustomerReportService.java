package com.saranaresturantsystem.reports.service.customer;

import com.saranaresturantsystem.reports.dto.customers.CustomerReportResponse;
import com.saranaresturantsystem.reports.specification.customers.CustomerReportFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerReportService {
    Page<CustomerReportResponse> getCustomersReport(CustomerReportFilter filter, Pageable pageable);
}