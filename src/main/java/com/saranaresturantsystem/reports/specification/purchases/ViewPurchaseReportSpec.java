package com.saranaresturantsystem.reports.specification.purchases;

import com.saranaresturantsystem.entities.purchase.PurchaseItem;
import com.saranaresturantsystem.reports.dto.purchases.PurchaseReportFilter;
import com.saranaresturantsystem.reports.entities.ViewPurchaseReport;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ViewPurchaseReportSpec {

    public static Specification<ViewPurchaseReport> filter(PurchaseReportFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("purchaseDate"), filter.startDate()));
            }
            if (filter.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("purchaseDate"), filter.endDate()));
            }
            if (filter.storeId() != null) {
                predicates.add(cb.equal(root.get("storeId"), filter.storeId()));
            }
            if (filter.supplierId() != null) {
                predicates.add(cb.equal(root.get("supplierId"), filter.supplierId()));
            }
            if (filter.status() != null && !filter.status().isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("status")), filter.status().toUpperCase()));
            }
            if (filter.paymentStatus() != null && !filter.paymentStatus().isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("paymentStatus")), filter.paymentStatus().toUpperCase()));
            }
            if (filter.productId() != null && query != null) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<PurchaseItem> itemRoot = subquery.from(PurchaseItem.class);
                subquery.select(itemRoot.get("purchase").get("id"))
                        .where(cb.equal(itemRoot.get("product").get("id"), filter.productId()));
                predicates.add(root.get("purchaseId").in(subquery));
            }
            if (filter.categoryId() != null && query != null) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<PurchaseItem> itemRoot = subquery.from(PurchaseItem.class);
                subquery.select(itemRoot.get("purchase").get("id"))
                        .where(cb.equal(itemRoot.get("product").get("models").get("category").get("id"), filter.categoryId()));
                predicates.add(root.get("purchaseId").in(subquery));
            }
            if (filter.brandId() != null && query != null) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<PurchaseItem> itemRoot = subquery.from(PurchaseItem.class);
                subquery.select(itemRoot.get("purchase").get("id"))
                        .where(cb.equal(itemRoot.get("product").get("models").get("brand").get("id"), filter.brandId()));
                predicates.add(root.get("purchaseId").in(subquery));
            }

            if (query != null) {
                query.orderBy(cb.desc(root.get("purchaseDate")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
