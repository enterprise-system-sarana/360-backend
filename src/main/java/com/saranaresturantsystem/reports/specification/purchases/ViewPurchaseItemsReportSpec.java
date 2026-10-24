package com.saranaresturantsystem.reports.specification.purchases;

import com.saranaresturantsystem.reports.dto.purchases.PurchaseReportFilter;
import com.saranaresturantsystem.reports.entities.ViewPurchaseItemsReport;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ViewPurchaseItemsReportSpec {

    public static Specification<ViewPurchaseItemsReport> filter(PurchaseReportFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("purchaseDate"), filter.startDate()));
            }
            if (filter.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("purchaseDate"), filter.endDate()));
            }
            if (filter.storeId() != null) {
                predicates.add(cb.equal(root.get("storeId"), filter.storeId()));
            }
            if (filter.supplierId() != null) {
                predicates.add(cb.equal(root.get("supplierId"), filter.supplierId()));
            }
            if (filter.productId() != null) {
                predicates.add(cb.equal(root.get("productId"), filter.productId()));
            }
            if (filter.categoryId() != null) {
                predicates.add(cb.equal(root.get("categoryId"), filter.categoryId()));
            }
            if (filter.brandId() != null) {
                predicates.add(cb.equal(root.get("brandId"), filter.brandId()));
            }
            if (filter.status() != null && !filter.status().isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("purchaseStatus")), filter.status().toUpperCase()));
            }
            if (filter.paymentStatus() != null && !filter.paymentStatus().isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("paymentStatus")), filter.paymentStatus().toUpperCase()));
            }

            if (query != null) {
                query.orderBy(cb.desc(root.get("purchaseDate")), cb.desc(root.get("itemId")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
