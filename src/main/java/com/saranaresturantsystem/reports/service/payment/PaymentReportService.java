package com.saranaresturantsystem.reports.service.payment;

import com.saranaresturantsystem.reports.dto.payment.PaymentReportFilter;
import com.saranaresturantsystem.reports.dto.payment.PurchasePaymentReportResponse;
import com.saranaresturantsystem.reports.dto.payment.PurchasePaymentSummaryResponse;
import com.saranaresturantsystem.reports.dto.payment.SalePaymentReportResponse;
import com.saranaresturantsystem.reports.dto.payment.SalePaymentSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentReportService {
    SalePaymentSummaryResponse getSalePaymentReport(PaymentReportFilter filter);
    Page<SalePaymentReportResponse> getSalePaymentReportPaged(PaymentReportFilter filter, Pageable pageable);

    PurchasePaymentSummaryResponse getPurchasePaymentReport(PaymentReportFilter filter);
    Page<PurchasePaymentReportResponse> getPurchasePaymentReportPaged(PaymentReportFilter filter, Pageable pageable);
}
