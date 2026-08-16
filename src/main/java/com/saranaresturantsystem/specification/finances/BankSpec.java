package com.saranaresturantsystem.specification.finances;

import com.saranaresturantsystem.entities.finances.Banks;
import org.springframework.data.jpa.domain.Specification;

public class BankSpec {
    public static Specification<Banks> filterBy(BankFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }
            var predicates = cb.conjunction();
            if (filter.name() != null && !filter.name().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("name")), "%" + filter.name().toUpperCase() + "%"));
            }
            if (filter.accountName() != null && !filter.accountName().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("accountName")), "%" + filter.name().toUpperCase() + "%"));
            }
            if (filter.accountNumber() != null && !filter.accountNumber().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("accountNumber")), "%" + filter.accountNumber().toUpperCase() + "%"));
            }
            return predicates;
        };
    }
}


