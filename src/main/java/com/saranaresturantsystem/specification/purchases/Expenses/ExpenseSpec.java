package com.saranaresturantsystem.specification.purchases.Expenses;

import com.saranaresturantsystem.entities.purchase.Expenses;
import org.springframework.data.jpa.domain.Specification;

public class ExpenseSpec {

    public static Specification<Expenses> filterBy(ExpenseFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }

            var predicates = cb.conjunction();

            // 🔍 ស្វែងរកតាម reference (Like & Upper)
            if (filter.reference() != null && !filter.reference().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("reference")), "%" + filter.reference().toUpperCase() + "%"));
            }

            // 👤 ស្វែងរកតាម createdBy (Like & Upper)
            if (filter.createdBy() != null && !filter.createdBy().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("createdBy")), "%" + filter.createdBy().toUpperCase() + "%"));
            }

            // 🏢 ស្វែងរកតាម storeId (Equal)
            if (filter.storeId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("storeId"), filter.storeId()));
            }

            // 🏦 ស្វែងរកតាម bankId (Equal)
            if (filter.bankId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("bankId"), filter.bankId()));
            }

            // 🏷️ ស្វែងរកតាម expenseTypeId (Equal)
            if (filter.expenseTypeId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("expenseTypeId"), filter.expenseTypeId()));
            }

            // 🟢 ស្វែងរកតាម status (Equal ឬ Like អាស្រ័យលើតម្រូវការ)
            if (filter.status() != null && !filter.status().isEmpty()) {
                predicates = cb.and(predicates, cb.equal(cb.upper(root.get("status")), filter.status().toUpperCase()));
            }

            return predicates;
        };
    }
}