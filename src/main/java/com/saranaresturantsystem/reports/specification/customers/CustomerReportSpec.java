package com.saranaresturantsystem.reports.specification.customers;

import com.saranaresturantsystem.entities.customer.Customer;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CustomerReportSpec {

    public static Specification<Customer> filter(CustomerReportFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter != null) {
                if (filter.getName() != null && !filter.getName().isBlank()) {
                    predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%"));
                }
                if (filter.getCode() != null && !filter.getCode().isBlank()) {
                    predicates.add(cb.like(cb.lower(root.get("code")), "%" + filter.getCode().toLowerCase() + "%"));
                }
                if (filter.getPhone() != null && !filter.getPhone().isBlank()) {
                    predicates.add(cb.like(cb.lower(root.get("phone")), "%" + filter.getPhone().toLowerCase() + "%"));
                }
                if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
                    predicates.add(cb.like(cb.lower(root.get("email")), "%" + filter.getEmail().toLowerCase() + "%"));
                }
                if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
                    predicates.add(cb.equal(root.get("status"), filter.getStatus()));
                }
            }

            assert query != null;
            query.orderBy(cb.desc(root.get("id")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}