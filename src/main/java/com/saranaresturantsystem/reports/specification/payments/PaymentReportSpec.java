package com.saranaresturantsystem.reports.specification.payments;

import com.saranaresturantsystem.entities.sales.Payment;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentReportSpec {

    public static Specification<Payment> filter(PaymentReportFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Object, Object> salesJoin = null;
            Join<Object, Object> customerJoin = null;
            Join<Object, Object> userJoin = null;
            Join<Object, Object> banksJoin = null;

            if (filter != null) {
                if (filter.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("paymentDate"), filter.getStartDate().atStartOfDay()));
                }

                if (filter.getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("paymentDate"), filter.getEndDate().atTime(LocalTime.MAX)));
                }
                if (filter.getSaleNo() != null && !filter.getSaleNo().isBlank()) {
                    salesJoin = root.join("sales", JoinType.LEFT);
                    predicates.add(cb.like(cb.lower(salesJoin.get("referenceNo")), "%" + filter.getSaleNo().toLowerCase() + "%"));
                }
                if (filter.getCustomerName() != null && !filter.getCustomerName().isBlank()) {
                    if (salesJoin == null) {
                        salesJoin = root.join("sales", JoinType.LEFT);
                    }
                    customerJoin = salesJoin.join("customer", JoinType.LEFT);
                    predicates.add(cb.like(cb.lower(customerJoin.get("name")), "%" + filter.getCustomerName().toLowerCase() + "%"));
                }
                if (filter.getCreatedBy() != null && !filter.getCreatedBy().isBlank()) {
                    userJoin = root.join("user", JoinType.LEFT);
                    Predicate usernamePredicate = cb.like(cb.lower(userJoin.get("username")), "%" + filter.getCreatedBy().toLowerCase() + "%");
                    Predicate emailPredicate = cb.like(cb.lower(userJoin.get("email")), "%" + filter.getCreatedBy().toLowerCase() + "%");
                    predicates.add(cb.or(usernamePredicate, emailPredicate));
                }
                if (filter.getPaidBy() != null && !filter.getPaidBy().isBlank()) {
                    banksJoin = root.join("banks", JoinType.LEFT);
                    Predicate methodPredicate = cb.like(cb.lower(root.get("paymentMethod")), "%" + filter.getPaidBy().toLowerCase() + "%");
                    Predicate bankNamePredicate = cb.like(cb.lower(banksJoin.get("name")), "%" + filter.getPaidBy().toLowerCase() + "%");
                    predicates.add(cb.or(methodPredicate, bankNamePredicate));
                }
            }

            assert query != null;
            query.orderBy(cb.desc(root.get("paymentDate")));
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}