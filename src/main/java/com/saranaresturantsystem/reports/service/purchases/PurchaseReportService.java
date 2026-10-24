package com.saranaresturantsystem.reports.service.purchases;

import com.saranaresturantsystem.reports.dto.purchases.PurchaseItemReportResponse;
import com.saranaresturantsystem.reports.dto.purchases.PurchaseReportFilter;
import com.saranaresturantsystem.reports.dto.purchases.PurchaseReportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PurchaseReportService {
    PurchaseReportResponse getPurchaseReport(PurchaseReportFilter filter);
    Page<PurchaseItemReportResponse> getPurchaseItemsReport(PurchaseReportFilter filter, Pageable pageable);
}
