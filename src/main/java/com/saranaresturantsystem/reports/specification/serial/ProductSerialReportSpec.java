package com.saranaresturantsystem.reports.specification.serial;

import com.saranaresturantsystem.entities.catalog.ProductSerials;
import com.saranaresturantsystem.reports.dto.serial.ProductSerialReportFilter;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ProductSerialReportSpec {

    public static Specification<ProductSerials> filter(ProductSerialReportFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.startDate().atStartOfDay()));
            }
            if (filter.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.endDate().atTime(LocalTime.MAX)));
            }
            if (filter.storeId() != null) {
                predicates.add(cb.equal(root.get("storeId"), filter.storeId()));
            }
            if (filter.productId() != null) {
                predicates.add(cb.equal(root.get("product").get("id"), filter.productId()));
            }
            if (filter.status() != null && !filter.status().isBlank()) {
                predicates.add(cb.equal(root.get("status"), filter.status()));
            }
            if (filter.barcode() != null && !filter.barcode().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("barcode")), "%" + filter.barcode().toLowerCase() + "%"));
            }

            assert query != null;
            query.orderBy(cb.desc(root.get("createdAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}