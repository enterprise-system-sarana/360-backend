package com.saranaresturantsystem.reports.specification.sales;

import com.saranaresturantsystem.reports.dto.sales.SaleReportFilter;
import com.saranaresturantsystem.reports.entities.ViewSalesItemsReport;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ViewSalesItemsReportSpec {

    public static Specification<ViewSalesItemsReport> filter(SaleReportFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("saleDate"), filter.startDate().atStartOfDay()));
            }
            if (filter.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("saleDate"), filter.endDate().atTime(LocalTime.MAX)));
            }
            if (filter.storeId() != null) {
                predicates.add(cb.equal(root.get("storeId"), filter.storeId()));
            }
            if (filter.customerId() != null) {
                predicates.add(cb.equal(root.get("customerId"), filter.customerId()));
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
            if (filter.saleStatus() != null && !filter.saleStatus().isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("saleStatus")), filter.saleStatus().trim().toUpperCase()));
            }
            if (filter.paymentStatus() != null && !filter.paymentStatus().isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("paymentStatus")), filter.paymentStatus().trim().toUpperCase()));
            }

            if (query != null) {
                query.orderBy(cb.desc(root.get("saleDate")), cb.desc(root.get("itemId")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
