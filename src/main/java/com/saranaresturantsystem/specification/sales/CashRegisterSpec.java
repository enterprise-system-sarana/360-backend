package com.saranaresturantsystem.specification.sales;

import com.saranaresturantsystem.entities.sales.CashRegister;
import com.saranaresturantsystem.specification.common.OrderBySpec;
import org.springframework.data.jpa.domain.Specification;

public class CashRegisterSpec {
    public static Specification<CashRegister> filter(CashRegisterFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (filter != null) {
                if (filter.storeId() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("store").get("id"), filter.storeId()));
                }

                if (filter.userId() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("user").get("id"), filter.userId()));
                }

                if (filter.status() != null && !filter.status().isEmpty()) {
                    predicates = cb.and(predicates, cb.equal(cb.upper(root.get("status")), filter.status().toUpperCase()));
                }
            }

            // Exclude soft-deleted records
            predicates = cb.and(predicates, cb.isNull(root.get("deletedAt")));

            OrderBySpec.applyLatestUpdatedFirst(root, query, cb);

            return predicates;
        };
    }
}
