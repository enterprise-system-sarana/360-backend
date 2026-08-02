package com.saranaresturantsystem.specification.catalog.model;

import com.saranaresturantsystem.entities.catalog.Model;
import org.springframework.data.jpa.domain.Specification;

public class ModelSpec {
    public  static Specification<Model> filterBy(ModelFilter filter){
        return (root, query, cb) -> {
            if(filter == null){
                return cb.conjunction();
            }
            var predicates = cb.conjunction();
            if (filter.categoryId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("categoryId"), filter.categoryId()));
            }
            if (filter.brandId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("brandId"), filter.brandId()));
            }
            if(filter.name() != null && !filter.name().isEmpty()){
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("name")), "%" + filter.name().toUpperCase() + "%"));
            }
            if(filter.status() != null && !filter.status().isEmpty()){
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("status")), "%" + filter.status().toUpperCase() + "%"));
            }
            return predicates;
        };
    }
}
