package com.saranaresturantsystem.specification.catalog.varianttype;

import com.saranaresturantsystem.entities.catalog.VariantType;
import org.springframework.data.jpa.domain.Specification;

public class VariantTypeSpec {

    public static Specification<VariantType> filterBy(VariantTypeFilter filter) {
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

            if (filter.name() != null && !filter.name().isEmpty()) {
                predicates = cb.and(
                        predicates,
                        cb.like(cb.upper(root.get("name")), "%" + filter.name().toUpperCase() + "%")
                );
            }

            return predicates;
        };
    }
}