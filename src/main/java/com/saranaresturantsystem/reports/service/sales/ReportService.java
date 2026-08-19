package com.saranaresturantsystem.reports.service.sales;

import com.saranaresturantsystem.reports.dto.sales.SaleReportFilter;
import com.saranaresturantsystem.reports.dto.sales.SaleReportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface ReportService {
    SaleReportResponse getSaleReport(SaleReportFilter filter);
    Page<SaleReportResponse> getSalesItemsReport(SaleReportFilter filter, Pageable pageable);
}