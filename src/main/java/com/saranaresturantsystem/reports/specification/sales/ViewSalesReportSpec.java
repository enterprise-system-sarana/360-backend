package com.saranaresturantsystem.reports.specification.sales;

import com.saranaresturantsystem.entities.sales.SaleItems;
import com.saranaresturantsystem.reports.dto.sales.SaleReportFilter;
import com.saranaresturantsystem.reports.entities.ViewSalesReport;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ViewSalesReportSpec {

    public static Specification<ViewSalesReport> filter(SaleReportFilter filter) {
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
            if (filter.saleStatus() != null && !filter.saleStatus().isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("saleStatus")), filter.saleStatus().toUpperCase()));
            }
            if (filter.paymentStatus() != null && !filter.paymentStatus().isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("paymentStatus")), filter.paymentStatus().toUpperCase()));
            }
            if (filter.productId() != null && query != null) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<SaleItems> itemRoot = subquery.from(SaleItems.class);
                subquery.select(itemRoot.get("sales").get("id"))
                        .where(cb.equal(itemRoot.get("product").get("id"), filter.productId()));
                predicates.add(root.get("saleId").in(subquery));
            }
            if (filter.categoryId() != null && query != null) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<SaleItems> itemRoot = subquery.from(SaleItems.class);
                subquery.select(itemRoot.get("sales").get("id"))
                        .where(cb.equal(itemRoot.get("product").get("models").get("category").get("id"), filter.categoryId()));
                predicates.add(root.get("saleId").in(subquery));
            }
            if (filter.brandId() != null && query != null) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<SaleItems> itemRoot = subquery.from(SaleItems.class);
                subquery.select(itemRoot.get("sales").get("id"))
                        .where(cb.equal(itemRoot.get("product").get("models").get("brand").get("id"), filter.brandId()));
                predicates.add(root.get("saleId").in(subquery));
            }

            if (query != null) {
                query.orderBy(cb.desc(root.get("saleDate")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
