package com.saranaresturantsystem.specification.inventory.stock;

import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.entities.inventory.Stock;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StockSpec {
    public static Specification<Stock> filter(StockFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.productId() != null) {
                predicates.add(cb.equal(root.get("product").get("id"), filter.productId()));
            }
            if (filter.storeId() != null) {
                predicates.add(cb.equal(root.get("stores").get("id"), filter.storeId()));
            }
            if (filter.status() != null && !filter.status().isBlank()) {
                String status = filter.status().trim().toUpperCase().replace("_", "");
                switch (status) {
                    case Constants.OUT_STOCK -> predicates.add(cb.or(
                            cb.isNull(root.get("quantity")),
                            cb.lessThanOrEqualTo(root.get("quantity"), BigDecimal.ZERO)
                    ));
                    case Constants.LOW_STOCK -> predicates.add(cb.and(
                            cb.isNotNull(root.get("quantity")),
                            cb.greaterThan(root.get("quantity"), BigDecimal.ZERO),
                            cb.isNotNull(root.get("alertQuantity")),
                            cb.greaterThan(root.get("alertQuantity"), BigDecimal.ZERO),
                            cb.lessThanOrEqualTo(root.get("quantity"), root.get("alertQuantity"))
                    ));
                    case Constants.IN_STOCK -> predicates.add(cb.and(
                            cb.isNotNull(root.get("quantity")),
                            cb.greaterThan(root.get("quantity"), BigDecimal.ZERO),
                            cb.or(
                                    cb.isNull(root.get("alertQuantity")),
                                    cb.lessThanOrEqualTo(root.get("alertQuantity"), BigDecimal.ZERO),
                                    cb.greaterThan(root.get("quantity"), root.get("alertQuantity"))
                            )
                    ));
                }
            }

            assert query != null;
            query.orderBy(cb.desc(root.get("id")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

    }
}
