package com.saranaresturantsystem.reports.specification.purchases;

import com.saranaresturantsystem.entities.purchase.Purchase;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PurchaseReportSpec {

    public static Specification<Purchase> filter(PurchaseReportFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter != null) {
                if (filter.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("purchaseDate"), filter.getStartDate()));
                }
                if (filter.getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("purchaseDate"), filter.getEndDate()));
                }
                if (filter.getStoreId() != null) {
                    predicates.add(cb.equal(root.get("stores").get("id"), filter.getStoreId()));
                }
                if (filter.getSupplierId() != null) {
                    predicates.add(cb.equal(root.get("suppliers").get("id"), filter.getSupplierId()));
                }
                if (filter.getBankId() != null) {
                    predicates.add(cb.equal(root.get("banks").get("id"), filter.getBankId()));
                }
                if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
                    predicates.add(cb.equal(root.get("status"), filter.getStatus()));
                }
                if (filter.getPaymentStatus() != null && !filter.getPaymentStatus().isBlank()) {
                    predicates.add(cb.equal(root.get("paymentStatus"), filter.getPaymentStatus()));
                }
                if (filter.getReferenceNo() != null && !filter.getReferenceNo().isBlank()) {
                    predicates.add(cb.like(cb.lower(root.get("referenceNo")), "%" + filter.getReferenceNo().toLowerCase() + "%"));
                }
            }

            assert query != null;
            query.orderBy(cb.desc(root.get("createdAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}