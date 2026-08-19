package com.saranaresturantsystem.specification.payment;

import com.saranaresturantsystem.entities.sales.Payment;
import org.springframework.data.jpa.domain.Specification;

public class PaymentSpec {
    public static Specification<Payment> filterBy(PaymentFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }
            var predicates = cb.conjunction();
            if (filter.paymentNo() != null && !filter.paymentNo().isBlank()) {
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("paymentNo")), "%" + filter.paymentNo().trim().toUpperCase() + "%"));
            }
            if (filter.saleId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("sales").get("id"), filter.saleId()));
            }
            if (filter.status() != null && !filter.status().isBlank()) {
                predicates = cb.and(predicates, cb.equal(cb.upper(root.get("status")), filter.status().trim().toUpperCase()));
            }
            if (filter.userId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("user").get("id"), filter.userId()));
            }
            if (filter.bank() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("banks").get("id"), filter.bank()));
            }
            return predicates;
        };
    }
}


