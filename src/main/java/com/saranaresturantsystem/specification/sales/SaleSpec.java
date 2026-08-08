package com.saranaresturantsystem.specification.sales;

import com.saranaresturantsystem.entities.sales.Sales;
import com.saranaresturantsystem.enums.SaleStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SaleSpec {
    public static Specification<Sales> filter(SaleFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.customerId() != null) {
                predicates.add(cb.equal(root.get("customerId"), filter.customerId()));
            }

            if (filter.status() != null && !filter.status().isEmpty()) {
                try {
                    predicates.add(cb.equal(root.get("saleStatus"),
                            SaleStatus.valueOf(filter.status().toUpperCase())));
                } catch (IllegalArgumentException ignored) {
                    predicates.add(cb.disjunction());
                }
            }

            predicates.add(cb.equal(root.get("deleteFlag"), 0));

            query.orderBy(cb.desc(root.get("id")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
