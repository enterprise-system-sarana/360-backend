package com.saranaresturantsystem.specification.catalog.product;

import com.saranaresturantsystem.entities.catalog.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpec {
    public  static Specification<Product> filterBy(ProductFilter filter){
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }
            var predicates = cb.conjunction();
            if (filter.code() != null && !filter.code().isEmpty()) {
                predicates = cb.and(
                        predicates,
                        cb.like(cb.upper(root.get("code")), "%" + filter.code().toUpperCase() + "%")
                );
            }
            if (filter.status() != null && !filter.status().isEmpty()) {
                predicates = cb.and(
                        predicates,
                        cb.like(cb.upper(root.get("code")), "%" + filter.status().toUpperCase() + "%")
                );
            }
            if (filter.modelId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("modelId"), filter.modelId()));
            }
            return predicates;
        };
    }
}
