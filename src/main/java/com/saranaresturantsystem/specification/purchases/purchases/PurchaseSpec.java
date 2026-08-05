package com.saranaresturantsystem.specification.purchases.purchases;

import com.saranaresturantsystem.entities.purchase.Purchases;
import org.springframework.data.jpa.domain.Specification;

public class PurchaseSpec {

    public static Specification<Purchases> filterBy(PurchaseFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }
            var predicates = cb.conjunction();

            // Filter by Reference Number
            if (filter.referenceNo() != null && !filter.referenceNo().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("referenceNo")), "%" + filter.referenceNo().toUpperCase() + "%"));
            }

            // Filter by Status
            if (filter.status() != null && !filter.status().isEmpty()) {
                predicates = cb.and(predicates, cb.equal(cb.upper(root.get("status")), filter.status().toUpperCase()));
            }

            // Filter by Supplier ID
            if (filter.supplierId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("supplierId"), filter.supplierId()));
            }

            // Filter by Store ID
            if (filter.storeId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("storeId"), filter.storeId()));
            }

            // Filter by Date Range (startDate to endDate)
            if (filter.startDate() != null && filter.endDate() != null) {
                predicates = cb.and(predicates, cb.between(root.get("purchaseDate"), filter.startDate(), filter.endDate()));
            } else if (filter.startDate() != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("purchaseDate"), filter.startDate()));
            } else if (filter.endDate() != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("purchaseDate"), filter.endDate()));
            }

            return predicates;
        };
    }
}