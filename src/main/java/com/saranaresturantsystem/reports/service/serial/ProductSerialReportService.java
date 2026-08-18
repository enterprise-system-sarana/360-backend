package com.saranaresturantsystem.reports.service.serial;

import com.saranaresturantsystem.reports.dto.serial.ProductSerialReportFilter;
import com.saranaresturantsystem.reports.dto.serial.ProductSerialReportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductSerialReportService {
    Page<ProductSerialReportResponse> getProductSerialsReport(ProductSerialReportFilter filter, Pageable pageable);
}