package com.saranaresturantsystem.specification.catalog.category;

import com.saranaresturantsystem.entities.catalog.Category;
import com.saranaresturantsystem.specification.common.StatusSpec;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpec {
    public  static Specification<Category> filterBy(CategoryFilter filter){
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            String status = filter != null ? filter.status() : null;
            predicates = cb.and(predicates, StatusSpec.filterStatus(root, cb, status));

            if (filter == null) {
                return predicates;
            }

            if (filter.code() != null && !filter.code().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("code")), "%" + filter.code().toUpperCase() + "%"));
            }
            if (filter.name() != null && !filter.name().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("name")), "%" + filter.name().toUpperCase() + "%"));
            }
            return predicates;
        };
    }
}
