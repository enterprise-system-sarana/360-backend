package com.saranaresturantsystem.specification.finances.bank;

import com.saranaresturantsystem.entities.finances.Banks;
import com.saranaresturantsystem.specification.common.StatusSpec;
import org.springframework.data.jpa.domain.Specification;

public class BankSpec {
    public static Specification<Banks> filterBy(BankFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            String status = filter != null ? filter.status() : null;
            predicates = cb.and(predicates, StatusSpec.filterStatus(root, cb, status));

            if (filter == null) {
                return predicates;
            }

            if (filter.name() != null && !filter.name().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("name")), "%" + filter.name().toUpperCase() + "%"));
            }
            if (filter.accountName() != null && !filter.accountName().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("accountName")), "%" + filter.accountName().toUpperCase() + "%"));
            }
            if (filter.accountNumber() != null && !filter.accountNumber().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("accountNumber")), "%" + filter.accountNumber().toUpperCase() + "%"));
            }
            return predicates;
        };
    }
}
