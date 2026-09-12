package com.saranaresturantsystem.reports.specification.customers;

import com.saranaresturantsystem.entities.sales.Sales;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AccountReceivableReportSpec {

    public static Specification<Sales> filter(AccountReceivableReportFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter != null) {
                // Change "createdAt" or "saleDate" depending on what your Sales entity uses
                if (filter.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getStartDate().atStartOfDay()));
                }
                if (filter.getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getEndDate().atTime(LocalTime.MAX)));
                }
                // Verify if store relationship is named "store" or "stores" in Sales entity
                if (filter.getStoreId() != null) {
                    predicates.add(cb.equal(root.get("store").get("id"), filter.getStoreId()));
                }
                if (filter.getCustomerName() != null && !filter.getCustomerName().isBlank()) {
                    predicates.add(cb.like(cb.lower(root.get("customer").get("name")), "%" + filter.getCustomerName().toLowerCase() + "%"));
                }
            }

            assert query != null;
            query.orderBy(cb.desc(root.get("createdAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}