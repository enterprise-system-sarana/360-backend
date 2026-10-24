package com.saranaresturantsystem.specification.sales;

import com.saranaresturantsystem.entities.sales.CashRegister;
import org.springframework.data.jpa.domain.Specification;

public class CashRegisterSpec {
    public static Specification<CashRegister> filter(CashRegisterFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }
            var predicates = cb.conjunction();

            if (filter.storeId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("store").get("id"), filter.storeId()));
            }

            if (filter.userId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("user").get("id"), filter.userId()));
            }

            if (filter.status() != null && !filter.status().isEmpty()) {
                predicates = cb.and(predicates, cb.equal(cb.upper(root.get("status")), filter.status().toUpperCase()));
            }

            // Exclude soft-deleted records
            predicates = cb.and(predicates, cb.isNull(root.get("deletedAt")));

            if (query != null) {
                query.orderBy(cb.desc(root.get("id")));
            }

            return predicates;
        };
    }
}
