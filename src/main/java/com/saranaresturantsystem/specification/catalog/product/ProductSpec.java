package com.saranaresturantsystem.specification.catalog.product;

import com.saranaresturantsystem.entities.catalog.Product;
import com.saranaresturantsystem.specification.common.StatusSpec;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpec {

    public static Specification<Product> filterBy(ProductFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            String status = filter != null ? filter.status() : null;
            predicates = cb.and(predicates, StatusSpec.filterStatus(root, cb, status));

            if (filter == null) {
                return predicates;
            }
            if (filter.name() != null && !filter.name().isBlank()) {
                predicates = cb.and(
                        predicates,
                        cb.equal(root.get("name"), filter.name())
                );
            }
            if (filter.code() != null && !filter.code().isBlank()) {
                predicates = cb.and(
                        predicates,
                        cb.equal(root.get("code"), filter.code())
                );
            }
            if (filter.name() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.join("name").get("id"), filter.modelId()));
            }


            if (filter.modelId() != null) {
                predicates = cb.and(predicates, cb.equal(root.join("models").get("id"), filter.modelId()));
            }

            return predicates;
        };
    }
}