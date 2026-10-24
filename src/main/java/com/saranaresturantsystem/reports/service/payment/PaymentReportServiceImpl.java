package com.saranaresturantsystem.reports.service.payment;

import com.saranaresturantsystem.entities.customer.Customer;
import com.saranaresturantsystem.entities.inventory.Stores;
import com.saranaresturantsystem.entities.purchase.Suppliers;
import com.saranaresturantsystem.repository.Inventory.StoreRepsoitory;
import com.saranaresturantsystem.repository.customer.CustomerRepository;
import com.saranaresturantsystem.repository.purchases.SupplierRepository;
import com.saranaresturantsystem.reports.dto.payment.*;
import com.saranaresturantsystem.reports.entities.ViewPurchasePaymentsReport;
import com.saranaresturantsystem.reports.entities.ViewSalePaymentsReport;
import com.saranaresturantsystem.reports.repository.ViewPurchasePaymentsReportRepository;
import com.saranaresturantsystem.reports.repository.ViewSalePaymentsReportRepository;
import com.saranaresturantsystem.reports.specification.payment.ViewPurchasePaymentsReportSpec;
import com.saranaresturantsystem.reports.specification.payment.ViewSalePaymentsReportSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentReportServiceImpl implements PaymentReportService {

    private final ViewSalePaymentsReportRepository viewSalePaymentsReportRepository;
    private final ViewPurchasePaymentsReportRepository viewPurchasePaymentsReportRepository;
    private final StoreRepsoitory storeRepository;
    private final CustomerRepository customerRepository;
    private final SupplierRepository supplierRepository;

    @Override
    @Transactional(readOnly = true)
    public SalePaymentSummaryResponse getSalePaymentReport(PaymentReportFilter filter) {
        LocalDateTime startDate = filter.startDate() != null ? filter.startDate().atStartOfDay() : null;
        LocalDateTime endDate = filter.endDate() != null ? filter.endDate().atTime(LocalTime.MAX) : null;

        String paymentStatus = filter.paymentStatus() != null && !filter.paymentStatus().isBlank() ? filter.paymentStatus().trim() : null;

        // 1. High-performance aggregated metrics from database view
        PaymentReportSummaryProjection summary = viewSalePaymentsReportRepository.getSummary(
                filter.storeId(),
                filter.customerId(),
                filter.saleId(),
                paymentStatus,
                startDate,
                endDate
        );

        double totalAmount = summary != null && summary.getTotalAmount() != null ? summary.getTotalAmount().doubleValue() : 0.0;
        double totalReceived = summary != null && summary.getTotalReceivedAmount() != null ? summary.getTotalReceivedAmount().doubleValue() : 0.0;
        double totalReturn = summary != null && summary.getTotalReturnAmount() != null ? summary.getTotalReturnAmount().doubleValue() : 0.0;
        double totalDue = summary != null && summary.getTotalDueAmount() != null ? summary.getTotalDueAmount().doubleValue() : 0.0;
        long totalTransactions = summary != null && summary.getTotalTransactions() != null ? summary.getTotalTransactions() : 0L;

        // 2. Fetch list of payments from database view
        Specification<ViewSalePaymentsReport> spec = ViewSalePaymentsReportSpec.filter(filter);
        List<ViewSalePaymentsReport> payments = viewSalePaymentsReportRepository.findAll(spec);

        List<SalePaymentReportResponse> paymentResponses = payments.stream().map(this::mapToSalePaymentResponse).toList();

        // 3. Store and Customer metadata
        Long storeId = filter.storeId();
        String storeName = null;
        if (storeId != null) {
            storeName = storeRepository.findById(storeId).map(Stores::getName).orElse(null);
        } else if (!payments.isEmpty()) {
            Set<String> storeNames = payments.stream().map(ViewSalePaymentsReport::getStoreName).filter(Objects::nonNull).collect(Collectors.toSet());
            if (storeNames.size() == 1) {
                storeName = storeNames.iterator().next();
                storeId = payments.get(0).getStoreId();
            } else if (storeNames.size() > 1) {
                storeName = "All Stores";
            }
        }

        Long customerId = filter.customerId();
        String customerName = null;
        if (customerId != null) {
            customerName = customerRepository.findById(customerId).map(Customer::getName).orElse(null);
        } else if (!payments.isEmpty()) {
            Set<String> customerNames = payments.stream().map(ViewSalePaymentsReport::getCustomerName).filter(Objects::nonNull).collect(Collectors.toSet());
            if (customerNames.size() == 1) {
                customerName = customerNames.iterator().next();
                customerId = payments.get(0).getCustomerId();
            } else if (customerNames.size() > 1) {
                customerName = "All Customers";
            }
        }

        if (startDate == null && !payments.isEmpty()) {
            startDate = payments.stream().map(ViewSalePaymentsReport::getPaymentDate).filter(Objects::nonNull).min(LocalDateTime::compareTo).orElse(null);
        }
        if (endDate == null && !payments.isEmpty()) {
            endDate = payments.stream().map(ViewSalePaymentsReport::getPaymentDate).filter(Objects::nonNull).max(LocalDateTime::compareTo).orElse(null);
        }

        return SalePaymentSummaryResponse.builder()
                .totalAmount(totalAmount)
                .totalReceivedAmount(totalReceived)
                .totalReturnAmount(totalReturn)
                .totalDueAmount(totalDue)
                .totalTransactions(totalTransactions)
                .storeId(storeId)
                .storeName(storeName)
                .customerId(customerId)
                .customerName(customerName)
                .startDate(startDate)
                .endDate(endDate)
                .payments(paymentResponses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalePaymentReportResponse> getSalePaymentReportPaged(PaymentReportFilter filter, Pageable pageable) {
        Specification<ViewSalePaymentsReport> spec = ViewSalePaymentsReportSpec.filter(filter);
        return viewSalePaymentsReportRepository.findAll(spec, pageable).map(this::mapToSalePaymentResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchasePaymentSummaryResponse getPurchasePaymentReport(PaymentReportFilter filter) {
        LocalDateTime startDate = filter.startDate() != null ? filter.startDate().atStartOfDay() : null;
        LocalDateTime endDate = filter.endDate() != null ? filter.endDate().atTime(LocalTime.MAX) : null;

        String paymentStatus = filter.paymentStatus() != null && !filter.paymentStatus().isBlank() ? filter.paymentStatus().trim() : null;

        // 1. High-performance aggregated metrics from database view
        PaymentReportSummaryProjection summary = viewPurchasePaymentsReportRepository.getSummary(
                filter.storeId(),
                filter.supplierId(),
                filter.purchaseId(),
                paymentStatus,
                startDate,
                endDate
        );

        double totalAmount = summary != null && summary.getTotalAmount() != null ? summary.getTotalAmount().doubleValue() : 0.0;
        double totalReceived = summary != null && summary.getTotalReceivedAmount() != null ? summary.getTotalReceivedAmount().doubleValue() : 0.0;
        double totalReturn = summary != null && summary.getTotalReturnAmount() != null ? summary.getTotalReturnAmount().doubleValue() : 0.0;
        double totalDue = summary != null && summary.getTotalDueAmount() != null ? summary.getTotalDueAmount().doubleValue() : 0.0;
        long totalTransactions = summary != null && summary.getTotalTransactions() != null ? summary.getTotalTransactions() : 0L;

        // 2. Fetch list of purchase payments from database view
        Specification<ViewPurchasePaymentsReport> spec = ViewPurchasePaymentsReportSpec.filter(filter);
        List<ViewPurchasePaymentsReport> payments = viewPurchasePaymentsReportRepository.findAll(spec);

        List<PurchasePaymentReportResponse> paymentResponses = payments.stream().map(this::mapToPurchasePaymentResponse).toList();

        // 3. Store and Supplier metadata
        Long storeId = filter.storeId();
        String storeName = null;
        if (storeId != null) {
            storeName = storeRepository.findById(storeId).map(Stores::getName).orElse(null);
        } else if (!payments.isEmpty()) {
            Set<String> storeNames = payments.stream().map(ViewPurchasePaymentsReport::getStoreName).filter(Objects::nonNull).collect(Collectors.toSet());
            if (storeNames.size() == 1) {
                storeName = storeNames.iterator().next();
                storeId = payments.get(0).getStoreId();
            } else if (storeNames.size() > 1) {
                storeName = "All Stores";
            }
        }

        Long supplierId = filter.supplierId();
        String supplierName = null;
        if (supplierId != null) {
            supplierName = supplierRepository.findById(supplierId).map(Suppliers::getName).orElse(null);
        } else if (!payments.isEmpty()) {
            Set<String> supplierNames = payments.stream().map(ViewPurchasePaymentsReport::getSupplierName).filter(Objects::nonNull).collect(Collectors.toSet());
            if (supplierNames.size() == 1) {
                supplierName = supplierNames.iterator().next();
                supplierId = payments.get(0).getSupplierId();
            } else if (supplierNames.size() > 1) {
                supplierName = "All Suppliers";
            }
        }

        if (startDate == null && !payments.isEmpty()) {
            startDate = payments.stream().map(ViewPurchasePaymentsReport::getPaymentDate).filter(Objects::nonNull).min(LocalDateTime::compareTo).orElse(null);
        }
        if (endDate == null && !payments.isEmpty()) {
            endDate = payments.stream().map(ViewPurchasePaymentsReport::getPaymentDate).filter(Objects::nonNull).max(LocalDateTime::compareTo).orElse(null);
        }

        return PurchasePaymentSummaryResponse.builder()
                .totalAmount(totalAmount)
                .totalReceivedAmount(totalReceived)
                .totalReturnAmount(totalReturn)
                .totalDueAmount(totalDue)
                .totalTransactions(totalTransactions)
                .storeId(storeId)
                .storeName(storeName)
                .supplierId(supplierId)
                .supplierName(supplierName)
                .startDate(startDate)
                .endDate(endDate)
                .payments(paymentResponses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchasePaymentReportResponse> getPurchasePaymentReportPaged(PaymentReportFilter filter, Pageable pageable) {
        Specification<ViewPurchasePaymentsReport> spec = ViewPurchasePaymentsReportSpec.filter(filter);
        return viewPurchasePaymentsReportRepository.findAll(spec, pageable).map(this::mapToPurchasePaymentResponse);
    }

    private SalePaymentReportResponse mapToSalePaymentResponse(ViewSalePaymentsReport v) {
        return SalePaymentReportResponse.builder()
                .paymentId(v.getPaymentId())
                .paymentNo(v.getPaymentNo())
                .paymentDate(v.getPaymentDate())
                .paymentStatus(v.getPaymentStatus())
                .totalAmount(v.getTotalAmount() != null ? v.getTotalAmount().doubleValue() : 0.0)
                .receivedAmount(v.getReceivedAmount() != null ? v.getReceivedAmount().doubleValue() : 0.0)
                .returnAmount(v.getReturnAmount() != null ? v.getReturnAmount().doubleValue() : 0.0)
                .dueAmount(v.getDueAmount() != null ? v.getDueAmount().doubleValue() : 0.0)
                .note(v.getNote())
                .saleId(v.getSaleId())
                .saleNo(v.getSaleNo())
                .saleReference(v.getSaleReference())
                .storeId(v.getStoreId())
                .storeName(v.getStoreName())
                .customerId(v.getCustomerId())
                .customerName(v.getCustomerName())
                .userId(v.getUserId())
                .cashierName(v.getCashierName())
                .paymentMethods(v.getPaymentMethods())
                .bankNames(v.getBankNames())
                .totalLinesCount(v.getTotalLinesCount())
                .build();
    }

    private PurchasePaymentReportResponse mapToPurchasePaymentResponse(ViewPurchasePaymentsReport v) {
        return PurchasePaymentReportResponse.builder()
                .paymentId(v.getPaymentId())
                .paymentNo(v.getPaymentNo())
                .paymentDate(v.getPaymentDate())
                .paymentStatus(v.getPaymentStatus())
                .totalAmount(v.getTotalAmount() != null ? v.getTotalAmount().doubleValue() : 0.0)
                .receivedAmount(v.getReceivedAmount() != null ? v.getReceivedAmount().doubleValue() : 0.0)
                .returnAmount(v.getReturnAmount() != null ? v.getReturnAmount().doubleValue() : 0.0)
                .dueAmount(v.getDueAmount() != null ? v.getDueAmount().doubleValue() : 0.0)
                .note(v.getNote())
                .purchaseId(v.getPurchaseId())
                .purchaseReferenceNo(v.getPurchaseReferenceNo())
                .storeId(v.getStoreId())
                .storeName(v.getStoreName())
                .supplierId(v.getSupplierId())
                .supplierName(v.getSupplierName())
                .userId(v.getUserId())
                .createdByUser(v.getCreatedByUser())
                .paymentMethods(v.getPaymentMethods())
                .bankNames(v.getBankNames())
                .totalLinesCount(v.getTotalLinesCount())
                .build();
    }
}
