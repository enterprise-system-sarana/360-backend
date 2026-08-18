package com.saranaresturantsystem.specification.quote;

import com.saranaresturantsystem.entities.quote.Quote;
import org.springframework.data.jpa.domain.Specification;

public class QuoteSpec {
    public static Specification<Quote> filterBy(QuoteFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }

            var predicates = cb.conjunction();

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

            if (filter.grandTotal() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("grandTotal"), filter.grandTotal()));
            }

            if (filter.discount() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("discount"), filter.discount()));
            }

            if (filter.status() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("status"), filter.status()));
            }

            if (filter.status_payment() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("status_payment"), filter.status_payment()));
            }

            if (filter.paid_amount() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("paid_amount"), filter.paid_amount()));
            }

            if (filter.return_amount() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("return_amount"), filter.return_amount()));
            }

            if (filter.noted() != null && !filter.noted().isBlank()) {
                predicates = cb.and(predicates,
                        cb.like(cb.upper(root.get("noted")),
                                "%" + filter.noted().toUpperCase() + "%"));
            }

            return predicates;
        };
    }
}
