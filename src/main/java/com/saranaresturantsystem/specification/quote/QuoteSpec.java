package com.saranaresturantsystem.specification.quote;

import com.saranaresturantsystem.entities.quote.Quote;
import com.saranaresturantsystem.specification.common.StatusSpec;
import org.springframework.data.jpa.domain.Specification;

public class QuoteSpec {
    public static Specification<Quote> filterBy(QuoteFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            String status = filter != null ? filter.status() : null;
            predicates = cb.and(predicates, StatusSpec.filterStatus(root, cb, status));

            if (filter == null) {
                return predicates;
            }

            if (filter.id() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("id"), filter.id()));
            }

            if (filter.date() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("date"), filter.date()));
            }

            if (filter.reference() != null && !filter.reference().isBlank()) {
                predicates = cb.and(predicates,
                        cb.like(cb.upper(root.get("reference")),
                                "%" + filter.reference().toUpperCase() + "%"));
            }

            if (filter.no() != null && !filter.no().isBlank()) {
                predicates = cb.and(predicates,
                        cb.like(cb.upper(root.get("no")),
                                "%" + filter.no().toUpperCase() + "%"));
            }

            if (filter.customerId() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("customer").get("id"), filter.customerId()));
            }

            if (filter.paymentStatus() != null && !filter.paymentStatus().isBlank()) {
                predicates = cb.and(predicates,
                        cb.equal(cb.upper(root.get("statusPayment")), filter.paymentStatus().trim().toUpperCase()));
            }

            return predicates;
        };
    }
}
