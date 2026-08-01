package com.saranaresturantsystem.specification.catalog.product;

import com.saranaresturantsystem.entities.catalog.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpec {
    public  static Specification<Product> filterBy(ProductFilter filter){
        return (root, query, cb) -> {
            if(filter == null){
                return cb.conjunction();
            }
            var predicates = cb.conjunction();
            if (filter.categoryId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("categoryId"), filter.categoryId()));
            }
            if (filter.brandId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("brandId"), filter.categoryId()));
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
