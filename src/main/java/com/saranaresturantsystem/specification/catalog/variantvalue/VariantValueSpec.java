package com.saranaresturantsystem.specification.catalog.variantvalue;

import com.saranaresturantsystem.entities.catalog.VariantValue;
import org.springframework.data.jpa.domain.Specification;

public class VariantValueSpec {

    public static Specification<VariantValue> filterBy(VariantValueFilter filter) {
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

            if (filter.variantTypeId() != null) {
                predicates = cb.and(
                        predicates,
                        cb.equal(root.get("variantTypeId"), filter.variantTypeId())
                );
            }

            return predicates;
        };
    }
}