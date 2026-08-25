package com.saranaresturantsystem.specification.purchases.Expenses;

import com.saranaresturantsystem.entities.purchase.Expenses;
import com.saranaresturantsystem.specification.common.StatusSpec;
import org.springframework.data.jpa.domain.Specification;

public class ExpenseSpec {

    public static Specification<Expenses> filterBy(ExpenseFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            String status = filter != null ? filter.status() : null;
            predicates = cb.and(predicates, StatusSpec.filterStatus(root, cb, status));

            if (filter == null) {
                return predicates;
            }

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

            return predicates;
        };
    }
}