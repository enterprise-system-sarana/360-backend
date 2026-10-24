package com.saranaresturantsystem.reports.specification.stock;

import com.saranaresturantsystem.reports.dto.stock.StockMovementReportFilter;
import com.saranaresturantsystem.reports.entities.ViewStockMovementReport;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ViewStockMovementReportSpec {

    public static Specification<ViewStockMovementReport> filter(StockMovementReportFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.storeId() != null) {
                predicates.add(cb.equal(root.get("storeId"), filter.storeId()));
            }
            if (filter.productId() != null) {
                predicates.add(cb.equal(root.get("productId"), filter.productId()));
            }
            if (filter.transactionType() != null && !filter.transactionType().isBlank() && !"ALL".equalsIgnoreCase(filter.transactionType().trim())) {
                predicates.add(cb.equal(cb.upper(root.get("transactionType")), filter.transactionType().trim().toUpperCase()));
            }
            if (filter.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), filter.startDate().atStartOfDay()));
            }
            if (filter.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), filter.endDate().atTime(LocalTime.MAX)));
            }
            if (filter.search() != null && !filter.search().isBlank()) {
                String pattern = "%" + filter.search().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("productName")), pattern),
                        cb.like(cb.lower(root.get("productCode")), pattern),
                        cb.like(cb.lower(root.get("storeName")), pattern)
                ));
            }

            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.orderBy(cb.desc(root.get("transactionDate")), cb.desc(root.get("transactionId")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
