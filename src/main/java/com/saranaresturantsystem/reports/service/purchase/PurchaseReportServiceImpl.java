package com.saranaresturantsystem.reports.service.purchase;

import com.saranaresturantsystem.entities.purchase.Purchase;
import com.saranaresturantsystem.reports.dto.purchases.PurchaseReportResponse;
import com.saranaresturantsystem.reports.specification.purchases.PurchaseReportFilter;
import com.saranaresturantsystem.reports.specification.purchases.PurchaseReportSpec;
import com.saranaresturantsystem.repository.purchases.PurchasesRepository; // Adjust to your actual purchase repository package
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseReportServiceImpl implements PurchaseReportService {

    private final PurchasesRepository purchaseRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseReportResponse> getPurchasesReport(PurchaseReportFilter filter, Pageable pageable) {
        Specification<Purchase> spec = PurchaseReportSpec.filter(filter);

        return purchaseRepository.findAll(spec, pageable).map(purchase ->
                PurchaseReportResponse.builder()
                        .id(purchase.getId())
                        .referenceNo(purchase.getReferenceNo())
                        .purchaseDate(purchase.getPurchaseDate())
                        .total(purchase.getTotal())
                        .discount(purchase.getDiscount())
                        .grandTotal(purchase.getGrandTotal())
                        .status(purchase.getStatus())
                        .paidAmount(purchase.getPaidAmount())
                        .dueAmount(purchase.getDueAmount())
                        .paymentStatus(purchase.getPaymentStatus())
                        .note(purchase.getNote())
                        .bankId(purchase.getBanks() != null ? purchase.getBanks().getId() : null)
                        .bankName(purchase.getBanks() != null ? purchase.getBanks().getName() : null)
                        .supplierId(purchase.getSuppliers() != null ? purchase.getSuppliers().getId() : null)
                        .supplierName(purchase.getSuppliers() != null ? purchase.getSuppliers().getName() : null)
                        .storeId(purchase.getStores() != null ? purchase.getStores().getId() : null)
                        .storeName(purchase.getStores() != null ? purchase.getStores().getName() : null)
                        .createdAt(purchase.getCreatedAt())
                        .build()
        );
    }
}