package com.saranaresturantsystem.reports.service.purchases;

import com.saranaresturantsystem.entities.inventory.Stores;
import com.saranaresturantsystem.entities.purchase.Suppliers;
import com.saranaresturantsystem.repository.Inventory.StoreRepsoitory;
import com.saranaresturantsystem.repository.purchases.SupplierRepository;
import com.saranaresturantsystem.reports.dto.purchases.PurchaseItemReportResponse;
import com.saranaresturantsystem.reports.dto.purchases.PurchaseReportFilter;
import com.saranaresturantsystem.reports.dto.purchases.PurchaseReportResponse;
import com.saranaresturantsystem.reports.dto.purchases.PurchaseReportSummaryProjection;
import com.saranaresturantsystem.reports.entities.ViewPurchaseItemsReport;
import com.saranaresturantsystem.reports.entities.ViewPurchaseReport;
import com.saranaresturantsystem.reports.repository.ViewPurchaseItemsReportRepository;
import com.saranaresturantsystem.reports.repository.ViewPurchaseReportRepository;
import com.saranaresturantsystem.reports.specification.purchases.ViewPurchaseItemsReportSpec;
import com.saranaresturantsystem.reports.specification.purchases.ViewPurchaseReportSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseReportServiceImpl implements PurchaseReportService {

    private final ViewPurchaseReportRepository viewPurchaseReportRepository;
    private final ViewPurchaseItemsReportRepository viewPurchaseItemsReportRepository;
    private final StoreRepsoitory storeRepository;
    private final SupplierRepository supplierRepository;

    @Override
    @Transactional(readOnly = true)
    public PurchaseReportResponse getPurchaseReport(PurchaseReportFilter filter) {
        String status = filter.status() != null && !filter.status().isBlank() ? filter.status().trim() : null;
        String paymentStatus = filter.paymentStatus() != null && !filter.paymentStatus().isBlank() ? filter.paymentStatus().trim() : null;

        // 1. High-performance aggregated metrics from database view
        PurchaseReportSummaryProjection summary = viewPurchaseReportRepository.getSummary(
                filter.storeId(),
                filter.supplierId(),
                status,
                paymentStatus,
                filter.startDate(),
                filter.endDate());

        double totalAmount = summary != null && summary.getTotalAmount() != null
                ? summary.getTotalAmount().doubleValue()
                : 0.0;
        double totalDiscount = summary != null && summary.getTotalDiscount() != null
                ? summary.getTotalDiscount().doubleValue()
                : 0.0;
        double totalPaidAmount = summary != null && summary.getTotalPaidAmount() != null
                ? summary.getTotalPaidAmount().doubleValue()
                : 0.0;
        double totalDueAmount = summary != null && summary.getTotalDueAmount() != null
                ? summary.getTotalDueAmount().doubleValue()
                : 0.0;
        double totalQuantity = summary != null && summary.getTotalQuantity() != null
                ? summary.getTotalQuantity().doubleValue()
                : 0.0;
        long totalTransactions = summary != null && summary.getTotalTransactions() != null
                ? summary.getTotalTransactions()
                : 0L;

        // 2. Fetch list of purchases from database view
        Specification<ViewPurchaseReport> spec = ViewPurchaseReportSpec.filter(filter);
        List<ViewPurchaseReport> viewPurchases = viewPurchaseReportRepository.findAll(spec);

        List<PurchaseReportResponse.PurchaseItemDetail> purchaseDetails = viewPurchases.stream()
                .map(p -> PurchaseReportResponse.PurchaseItemDetail.builder()
                        .purchaseId(p.getPurchaseId())
                        .referenceNo(p.getReferenceNo())
                        .purchaseDate(p.getPurchaseDate())
                        .status(p.getStatus())
                        .paymentStatus(p.getPaymentStatus())
                        .total(p.getTotal() != null ? p.getTotal().doubleValue() : 0.0)
                        .discount(p.getDiscount() != null ? p.getDiscount().doubleValue() : 0.0)
                        .grandTotal(p.getGrandTotal() != null ? p.getGrandTotal().doubleValue() : 0.0)
                        .paidAmount(p.getPaidAmount() != null ? p.getPaidAmount().doubleValue() : 0.0)
                        .dueAmount(p.getDueAmount() != null ? p.getDueAmount().doubleValue() : 0.0)
                        .storeId(p.getStoreId())
                        .storeName(p.getStoreName())
                        .supplierId(p.getSupplierId())
                        .supplierName(p.getSupplierName())
                        .bankId(p.getBankId())
                        .bankName(p.getBankName())
                        .totalItemsCount(p.getTotalItemsCount())
                        .totalQuantity(p.getTotalQuantity() != null ? p.getTotalQuantity().doubleValue() : 0.0)
                        .build())
                .toList();

        // 3. Store and Supplier Metadata
        Long storeId = filter.storeId();
        String storeName = null;
        if (storeId != null) {
            storeName = storeRepository.findById(storeId).map(Stores::getName).orElse(null);
        } else if (!viewPurchases.isEmpty()) {
            Set<String> storeNames = viewPurchases.stream().map(ViewPurchaseReport::getStoreName)
                    .filter(Objects::nonNull).collect(Collectors.toSet());
            if (storeNames.size() == 1) {
                storeName = storeNames.iterator().next();
                storeId = viewPurchases.get(0).getStoreId();
            } else if (storeNames.size() > 1) {
                storeName = "All Stores";
            }
        }

        Long supplierId = filter.supplierId();
        String supplierName = null;
        if (supplierId != null) {
            supplierName = supplierRepository.findById(supplierId).map(Suppliers::getName).orElse(null);
        } else if (!viewPurchases.isEmpty()) {
            Set<String> supplierNames = viewPurchases.stream().map(ViewPurchaseReport::getSupplierName)
                    .filter(Objects::nonNull).collect(Collectors.toSet());
            if (supplierNames.size() == 1) {
                supplierName = supplierNames.iterator().next();
                supplierId = viewPurchases.get(0).getSupplierId();
            } else if (supplierNames.size() > 1) {
                supplierName = "All Suppliers";
            }
        }

        LocalDate startDate = filter.startDate();
        LocalDate endDate = filter.endDate();
        if (startDate == null && !viewPurchases.isEmpty()) {
            startDate = viewPurchases.stream().map(ViewPurchaseReport::getPurchaseDate).filter(Objects::nonNull)
                    .min(LocalDate::compareTo).orElse(null);
        }
        if (endDate == null && !viewPurchases.isEmpty()) {
            endDate = viewPurchases.stream().map(ViewPurchaseReport::getPurchaseDate).filter(Objects::nonNull)
                    .max(LocalDate::compareTo).orElse(null);
        }

        return PurchaseReportResponse.builder()
                .totalAmount(totalAmount)
                .totalDiscount(totalDiscount)
                .totalPaidAmount(totalPaidAmount)
                .totalDueAmount(totalDueAmount)
                .totalQuantity(totalQuantity)
                .totalTransactions(totalTransactions)
                .storeId(storeId)
                .storeName(storeName)
                .supplierId(supplierId)
                .supplierName(supplierName)
                .startDate(startDate)
                .endDate(endDate)
                .purchases(purchaseDetails)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseItemReportResponse> getPurchaseItemsReport(PurchaseReportFilter filter, Pageable pageable) {
        Specification<ViewPurchaseItemsReport> spec = ViewPurchaseItemsReportSpec.filter(filter);

        return viewPurchaseItemsReportRepository.findAll(spec, pageable)
                .map(item -> PurchaseItemReportResponse.builder()
                        .itemId(item.getItemId())
                        .purchaseId(item.getPurchaseId())
                        .referenceNo(item.getReferenceNo())
                        .purchaseDate(item.getPurchaseDate())
                        .purchaseStatus(item.getPurchaseStatus())
                        .paymentStatus(item.getPaymentStatus())
                        .storeId(item.getStoreId())
                        .storeName(item.getStoreName())
                        .supplierId(item.getSupplierId())
                        .supplierName(item.getSupplierName())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productCode(item.getProductCode())
                        .modelId(item.getModelId())
                        .modelName(item.getModelName())
                        .brandId(item.getBrandId())
                        .brandName(item.getBrandName())
                        .categoryId(item.getCategoryId())
                        .categoryName(item.getCategoryName())
                        .quantity(item.getQuantity() != null ? item.getQuantity().doubleValue() : 0.0)
                        .unitCost(item.getUnitCost() != null ? item.getUnitCost().doubleValue() : 0.0)
                        .unitPrice(item.getUnitPrice() != null ? item.getUnitPrice().doubleValue() : 0.0)
                        .subtotal(item.getSubtotal() != null ? item.getSubtotal().doubleValue() : 0.0)
                        .build());
    }
}
