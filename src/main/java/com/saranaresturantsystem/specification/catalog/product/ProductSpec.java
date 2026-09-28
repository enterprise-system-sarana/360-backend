package com.saranaresturantsystem.specification.catalog.product;

import com.saranaresturantsystem.entities.catalog.Product;
import com.saranaresturantsystem.specification.common.StatusSpec;
import jakarta.persistence.criteria.Join;
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
            if (filter.modelId() != null || filter.categoryId() != null || filter.brandId() != null) {
                Join<Product, ?> model = root.join("models");

                if (filter.modelId() != null) {
                    predicates = cb.and(predicates, cb.equal(model.get("id"), filter.modelId()));
                }
                if (filter.categoryId() != null) {
                    predicates = cb.and(predicates, cb.equal(model.get("category").get("id"), filter.categoryId()));
                }
                if (filter.brandId() != null) {
                    predicates = cb.and(predicates, cb.equal(model.get("brand").get("id"), filter.brandId()));
                }
            }

            return predicates;
        };
    }
}