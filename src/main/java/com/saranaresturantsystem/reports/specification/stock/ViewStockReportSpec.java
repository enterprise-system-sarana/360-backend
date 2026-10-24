package com.saranaresturantsystem.reports.specification.stock;

import com.saranaresturantsystem.reports.dto.stock.StockReportFilter;
import com.saranaresturantsystem.reports.entities.ViewStockReport;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ViewStockReportSpec {

    public static Specification<ViewStockReport> filter(StockReportFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.storeId() != null) {
                predicates.add(cb.equal(root.get("storeId"), filter.storeId()));
            }
            if (filter.productId() != null) {
                predicates.add(cb.equal(root.get("productId"), filter.productId()));
            }
            if (filter.categoryId() != null) {
                predicates.add(cb.equal(root.get("categoryId"), filter.categoryId()));
            }
            if (filter.brandId() != null) {
                predicates.add(cb.equal(root.get("brandId"), filter.brandId()));
            }
            if (filter.stockStatus() != null && !filter.stockStatus().isBlank() && !"ALL".equalsIgnoreCase(filter.stockStatus().trim())) {
                predicates.add(cb.equal(cb.upper(root.get("stockStatus")), filter.stockStatus().trim().toUpperCase()));
            }
            if (Boolean.TRUE.equals(filter.lowStockOnly())) {
                predicates.add(cb.or(
                        cb.equal(root.get("stockStatus"), "LOW_STOCK"),
                        cb.equal(root.get("stockStatus"), "OUT_OF_STOCK")
                ));
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
                query.orderBy(cb.asc(root.get("storeName")), cb.asc(root.get("productName")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
