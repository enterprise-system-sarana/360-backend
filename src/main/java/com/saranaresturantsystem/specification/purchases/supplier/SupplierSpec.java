package com.saranaresturantsystem.specification.purchases.supplier;

import com.saranaresturantsystem.entities.purchase.Suppliers;
import org.springframework.data.jpa.domain.Specification;

public class SupplierSpec{
    public  static Specification<Suppliers> filterBy(SupplierFilter filter){
        return (root, query, cb) -> {
            if(filter == null){
                return cb.conjunction();
            }
            var predicates = cb.conjunction();
            if(filter.code() != null && !filter.code().isEmpty()){
                predicates = cb.and(predicates, cb.like(cb.upper(root.get("code")), "%" + filter.code().toUpperCase() + "%"));
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
