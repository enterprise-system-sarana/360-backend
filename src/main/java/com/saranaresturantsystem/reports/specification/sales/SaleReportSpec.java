package com.saranaresturantsystem.reports.specification.sales;

import com.saranaresturantsystem.entities.sales.SaleItems;
import com.saranaresturantsystem.entities.sales.Sales;
import com.saranaresturantsystem.reports.dto.sales.SaleReportFilter;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SaleReportSpec {

    public static Specification<Sales> filter(SaleReportFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), filter.startDate().atStartOfDay()));
            }
            if (filter.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), filter.endDate().atTime(LocalTime.MAX)));
            }
            if (filter.storeId() != null) {
                predicates.add(cb.equal(root.get("storeId"), filter.storeId()));
            }
            if (filter.customerId() != null) {
                predicates.add(cb.equal(root.get("customerId"), filter.customerId()));
            }
            if (filter.saleStatus() != null && !filter.saleStatus().isBlank()) {
                predicates.add(cb.equal(root.get("saleStatus"), filter.saleStatus()));
            }
            if (filter.paymentStatus() != null && !filter.paymentStatus().isBlank()) {
                predicates.add(cb.equal(root.get("paymentStatus"), filter.paymentStatus()));
            }
            if (filter.productId() != null) {
                assert query != null;
                Join<Sales, SaleItems> itemJoin = root.join("items");
                predicates.add(cb.equal(itemJoin.get("product").get("id"), filter.productId()));
                query.distinct(true);
            }

            assert query != null;
            query.orderBy(cb.desc(root.get("date")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}