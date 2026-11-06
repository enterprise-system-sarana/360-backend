package com.saranaresturantsystem.specification.customer;

import com.saranaresturantsystem.entities.customer.Customer;
import com.saranaresturantsystem.specification.common.OrderBySpec;
import com.saranaresturantsystem.specification.common.StatusSpec;
import org.springframework.data.jpa.domain.Specification;

public class CustomerSpec {
    public static Specification<Customer> filterBy(CustomerFilter filter){
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            String status = filter != null ? filter.status() : null;
            predicates = cb.and(predicates, StatusSpec.filterStatus(root, cb, status));

            if (filter != null) {
                if (filter.code() != null && !filter.code().isEmpty()) {
                    predicates = cb.and(predicates, cb.like(cb.upper(root.get("code")), "%" + filter.code().toUpperCase() + "%"));
                }
                if (filter.name() != null && !filter.name().isEmpty()) {
                    predicates = cb.and(predicates, cb.like(cb.upper(root.get("name")), "%" + filter.name().toUpperCase() + "%"));
                }
            }

            OrderBySpec.applyLatestUpdatedFirst(root, query, cb);

            return predicates;
        };
    }
}
