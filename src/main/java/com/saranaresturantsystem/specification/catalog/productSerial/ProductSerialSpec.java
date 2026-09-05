package com.saranaresturantsystem.specification.catalog.productSerial;

import com.saranaresturantsystem.entities.catalog.ProductSerials;
import com.saranaresturantsystem.entities.catalog.VariantType;
import com.saranaresturantsystem.specification.catalog.varianttype.VariantTypeFilter;
import com.saranaresturantsystem.specification.common.StatusSpec;
import org.springframework.data.jpa.domain.Specification;

public class ProductSerialSpec {
    public static Specification<ProductSerials> filterBy(ProductSerialFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
//            String status = filter != null ? filter.status() : null;
//            predicates = cb.and(predicates, StatusSpec.filterStatus(root, cb, status));

            if (filter == null) {
                return predicates;
            }

            if (filter.barcode() != null && !filter.barcode().isEmpty()) {
                predicates = cb.and(
                        predicates,
                        cb.like(cb.upper(root.get("barCode")), "%" + filter.barcode().toUpperCase() + "%")
                );
            }

//            if (filter.storeId() != null) {
//                predicates = cb.and(
//                        predicates,
//                        cb.equal(root.get("store").get("id"), filter.storeId())
//                );
//            }

 if (filter.productId() != null) {
                predicates = cb.and(
                        predicates,
                        cb.equal(root.get("product").get("id"), filter.productId())
                );
            }


            return predicates;
        };
    }
}
