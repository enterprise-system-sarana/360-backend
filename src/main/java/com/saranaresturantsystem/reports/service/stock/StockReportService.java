package com.saranaresturantsystem.reports.service.stock;

import com.saranaresturantsystem.reports.dto.stock.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StockReportService {

    StockReportResponse getStockReport(StockReportFilter filter);

    Page<StockItemReportResponse> getStockReportPaged(StockReportFilter filter, Pageable pageable);

    StockReportSummaryProjection getStockSummary(StockReportFilter filter);

    Page<StockMovementItemResponse> getStockMovements(StockMovementReportFilter filter, Pageable pageable);
}
