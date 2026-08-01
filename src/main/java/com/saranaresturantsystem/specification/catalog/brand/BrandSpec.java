package com.saranaresturantsystem.specification.catalog.brand;

import com.saranaresturantsystem.entities.catalog.Brands;
import org.springframework.data.jpa.domain.Specification;

public class BrandSpec {
    public  static Specification<Brands> filterBy(BrandFilter filter){
        return (root, query, cb) -> {
            if(filter == null){
                return cb.conjunction();
            }
            var predicates = cb.conjunction();
            if(filter.name() != null && !filter.name().isEmpty()){
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("name")), "%" + filter.name().toUpperCase() + "%"));
            }
            return predicates;
        };
    }
}
