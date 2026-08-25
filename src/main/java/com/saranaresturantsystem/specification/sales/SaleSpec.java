package com.saranaresturantsystem.specification.sales;

import com.saranaresturantsystem.entities.sales.Sales;
import org.springframework.data.jpa.domain.Specification;

public class SaleSpec {
    public static Specification<Sales> filter(SaleFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }
            var predicates = cb.conjunction();
            if (filter.customerId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("customerId"), filter.customerId()));
            }

            if (filter.status() != null && !filter.status().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("saleStatus")), "%" + filter.status().toUpperCase() + "%"));
            }

            if (query != null) {
                query.orderBy(cb.desc(root.get("id")));
            }

            return predicates;
        };
    }
}
