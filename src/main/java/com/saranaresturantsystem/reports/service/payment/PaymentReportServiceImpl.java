package com.saranaresturantsystem.reports.service.payment;

import com.saranaresturantsystem.entities.sales.Payment;
import com.saranaresturantsystem.reports.dto.payments.PaymentReportResponse;
import com.saranaresturantsystem.reports.specification.payments.PaymentReportFilter;
import com.saranaresturantsystem.reports.specification.payments.PaymentReportSpec;
import com.saranaresturantsystem.repository.sales.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentReportServiceImpl implements PaymentReportService {

    private final PaymentRepository paymentRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentReportResponse> getPaymentsReport(PaymentReportFilter filter, Pageable pageable) {
        Specification<Payment> spec = PaymentReportSpec.filter(filter);

        return paymentRepository.findAll(spec, pageable).map(payment ->
                PaymentReportResponse.builder()
                        .id(payment.getId())
                        .date(payment.getPaymentDate() != null ? payment.getPaymentDate().toLocalDate() : null)
                        .paymentReference(payment.getPaymentNo())
                        .saleNo(payment.getSales() != null ? payment.getSales().getReference() : null)
                        .customerName(payment.getSales() != null && payment.getSales().getCustomer() != null ? payment.getSales().getCustomer().getName() : "Walk-in")
                        .paidBy(payment.getPaymentMethod())
                        .amount(payment.getAmount())
                        .createdBy(payment.getUser() != null ? payment.getUser().getUsername() : null)
                        .build()
        );
    }
}