package com.saranaresturantsystem.specification.sales;

import com.saranaresturantsystem.entities.sales.Sales;
import com.saranaresturantsystem.specification.common.OrderBySpec;
import org.springframework.data.jpa.domain.Specification;

public class SaleSpec {
    public static Specification<Sales> filter(SaleFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (filter != null) {
                if (filter.customerId() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("customer").get("id"), filter.customerId()));
                }

                if (filter.status() != null && !filter.status().isEmpty()) {
                    predicates = cb.and(predicates, cb.like(cb.upper(root.get("saleStatus")), "%" + filter.status().toUpperCase() + "%"));
                }
            }

            OrderBySpec.applyLatestUpdatedFirst(root, query, cb);

            return predicates;
        };
    }
}
