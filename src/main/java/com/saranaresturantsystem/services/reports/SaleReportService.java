package com.saranaresturantsystem.services.reports;

import com.saranaresturantsystem.dto.response.reports.SalesReportResponse;

import java.time.LocalDateTime;

public interface SaleReportService {
    SalesReportResponse getSalesReport(LocalDateTime start, LocalDateTime end, Long storeId);

}
