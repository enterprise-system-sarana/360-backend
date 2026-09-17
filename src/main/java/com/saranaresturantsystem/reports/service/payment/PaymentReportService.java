package com.saranaresturantsystem.reports.service.payment;

import com.saranaresturantsystem.reports.dto.payments.PaymentReportResponse;
import com.saranaresturantsystem.reports.specification.payments.PaymentReportFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentReportService {
    Page<PaymentReportResponse> getPaymentsReport(PaymentReportFilter filter, Pageable pageable);
}