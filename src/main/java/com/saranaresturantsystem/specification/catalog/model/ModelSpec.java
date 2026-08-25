package com.saranaresturantsystem.specification.catalog.model;

import com.saranaresturantsystem.entities.catalog.Model;
import com.saranaresturantsystem.specification.common.StatusSpec;
import org.springframework.data.jpa.domain.Specification;

public class ModelSpec {
    public  static Specification<Model> filterBy(ModelFilter filter){
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            String status = filter != null ? filter.status() : null;
            predicates = cb.and(predicates, StatusSpec.filterStatus(root, cb, status));

            if (filter == null) {
                return predicates;
            }

            if (filter.categoryId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("categoryId"), filter.categoryId()));
            }
            if (filter.brandId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("brandId"), filter.brandId()));
            }
            if (filter.name() != null && !filter.name().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("name")), "%" + filter.name().toUpperCase() + "%"));
            }
            return predicates;
        };
    }
}
