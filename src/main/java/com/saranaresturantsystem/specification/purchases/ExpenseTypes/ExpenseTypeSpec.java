package com.saranaresturantsystem.specification.purchases.ExpenseTypes;

import com.saranaresturantsystem.entities.purchase.ExpenseType;
import org.springframework.data.jpa.domain.Specification;

public class ExpenseTypeSpec {

    public static Specification<ExpenseType> filterBy(ExpenseTypeFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }

            var predicates = cb.conjunction();

            // 🔍 ស្វែងរកតាម code (Like & Upper)
            if (filter.code() != null && !filter.code().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("code")), "%" + filter.code().toUpperCase() + "%"));
            }

            // 🏷️ ស្វែងរកតាម name (Like & Upper)
            if (filter.name() != null && !filter.name().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("name")), "%" + filter.name().toUpperCase() + "%"));
            }

            // 🟢 ស្វែងរកតាម status (Equal)
            if (filter.status() != null && !filter.status().isEmpty()) {
                predicates = cb.and(predicates, cb.equal(cb.upper(root.get("status")), filter.status().toUpperCase()));
            }

            return predicates;
        };
    }
}