package com.saranaresturantsystem.specification.catalog.productSerial;

import com.saranaresturantsystem.entities.catalog.ProductSerials;
import com.saranaresturantsystem.specification.common.OrderBySpec;
import org.springframework.data.jpa.domain.Specification;

public class ProductSerialSpec {
    public static Specification<ProductSerials> filterBy(ProductSerialFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter != null) {
                if (filter.barcode() != null && !filter.barcode().isEmpty()) {
                    predicates = cb.and(
                            predicates,
                            cb.like(cb.upper(root.get("barCode")), "%" + filter.barcode().toUpperCase() + "%")
                    );
                }

                if (filter.productId() != null) {
                    predicates = cb.and(
                            predicates,
                            cb.equal(root.get("product").get("id"), filter.productId())
                    );
                }
            }

            OrderBySpec.applyLatestUpdatedFirst(root, query, cb);

            return predicates;
        };
    }
}
