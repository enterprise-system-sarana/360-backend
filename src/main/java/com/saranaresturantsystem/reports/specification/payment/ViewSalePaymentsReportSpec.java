package com.saranaresturantsystem.reports.specification.payment;

import com.saranaresturantsystem.entities.sales.PaymentLine;
import com.saranaresturantsystem.reports.dto.payment.PaymentReportFilter;
import com.saranaresturantsystem.reports.entities.ViewSalePaymentsReport;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ViewSalePaymentsReportSpec {

    public static Specification<ViewSalePaymentsReport> filter(PaymentReportFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("paymentDate"), filter.startDate().atStartOfDay()));
            }
            if (filter.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("paymentDate"), filter.endDate().atTime(LocalTime.MAX)));
            }
            if (filter.storeId() != null) {
                predicates.add(cb.equal(root.get("storeId"), filter.storeId()));
            }
            if (filter.customerId() != null) {
                predicates.add(cb.equal(root.get("customerId"), filter.customerId()));
            }
            if (filter.saleId() != null) {
                predicates.add(cb.equal(root.get("saleId"), filter.saleId()));
            }
            if (filter.paymentStatus() != null && !filter.paymentStatus().isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("paymentStatus")), filter.paymentStatus().toUpperCase()));
            }
            if (filter.paymentMethod() != null && !filter.paymentMethod().isBlank()) {
                predicates.add(cb.like(cb.upper(root.get("paymentMethods")), "%" + filter.paymentMethod().toUpperCase().trim() + "%"));
            }
            if (filter.bankId() != null && query != null) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<PaymentLine> lineRoot = subquery.from(PaymentLine.class);
                subquery.select(lineRoot.get("payment").get("id"))
                        .where(cb.equal(lineRoot.get("bank").get("id"), filter.bankId()));
                predicates.add(root.get("paymentId").in(subquery));
            }

            if (query != null) {
                query.orderBy(cb.desc(root.get("paymentDate")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
