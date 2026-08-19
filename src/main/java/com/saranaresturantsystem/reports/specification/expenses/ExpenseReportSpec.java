package com.saranaresturantsystem.reports.specification.expenses;


import com.saranaresturantsystem.entities.purchase.Expenses;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ExpenseReportSpec {

    public static Specification<Expenses> filter(ExpenseReportFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter != null) {
                if (filter.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getStartDate().atStartOfDay()));
                }
                if (filter.getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getEndDate().atTime(LocalTime.MAX)));
                }
                if (filter.getStoreId() != null) {
                    predicates.add(cb.equal(root.get("storeId"), filter.getStoreId()));
                }
                if (filter.getBankId() != null) {
                    predicates.add(cb.equal(root.get("bank").get("id"), filter.getBankId()));
                }
                if (filter.getExpenseTypeId() != null) {
                    predicates.add(cb.equal(root.get("expenseType").get("id"), filter.getExpenseTypeId()));
                }
                if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
                    predicates.add(cb.equal(root.get("status"), filter.getStatus()));
                }
                if (filter.getReference() != null && !filter.getReference().isBlank()) {
                    predicates.add(cb.like(cb.lower(root.get("reference")), "%" + filter.getReference().toLowerCase() + "%"));
                }
            }

            assert query != null;
            query.orderBy(cb.desc(root.get("createdAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}