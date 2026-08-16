package com.saranaresturantsystem.specification.inventory.stock;

import com.saranaresturantsystem.entities.inventory.Stock;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class StockSpec {
    public  static Specification<Stock> filter(StockFilter filter){
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.productId() != null) {
                predicates.add(cb.equal(root.get("productId"), filter.productId()));
            }     if (filter.storeId() != null) {
                predicates.add(cb.equal(root.get("storeId"), filter.storeId()));
            }


            assert query != null;
            query.orderBy(cb.desc(root.get("id")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

    }
}
