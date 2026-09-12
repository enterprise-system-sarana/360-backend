package com.saranaresturantsystem.reports.service.purchase;

import com.saranaresturantsystem.reports.dto.purchases.PurchaseReportResponse;
import com.saranaresturantsystem.reports.specification.purchases.PurchaseReportFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PurchaseReportService {
    Page<PurchaseReportResponse> getPurchasesReport(PurchaseReportFilter filter, Pageable pageable);
}