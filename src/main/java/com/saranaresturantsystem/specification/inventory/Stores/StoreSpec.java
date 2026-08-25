package com.saranaresturantsystem.specification.inventory.Stores;

import com.saranaresturantsystem.entities.inventory.Stores;
import com.saranaresturantsystem.specification.common.StatusSpec;
import org.springframework.data.jpa.domain.Specification;

public class StoreSpec {
    public static Specification<Stores> filterBy(StoreFilter filter){
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
            if (filter.code() != null && !filter.code().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("code")), "%" + filter.code().toUpperCase() + "%"));
            }
            if (filter.email() != null && !filter.email().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("email")), "%" + filter.email().toUpperCase() + "%"));
            }
            if (filter.phone() != null && !filter.phone().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("phone")), "%" + filter.phone().toUpperCase() + "%"));
            }
            if (filter.city() != null && !filter.city().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("city")), "%" + filter.city().toUpperCase() + "%"));
            }
            return predicates;
        };
    }
}
