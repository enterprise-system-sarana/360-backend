package com.saranaresturantsystem.specification.inventory.stock;

import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.entities.catalog.Model;
import com.saranaresturantsystem.entities.catalog.Product;
import com.saranaresturantsystem.entities.inventory.Stock;
import com.saranaresturantsystem.entities.inventory.Stores;
import com.saranaresturantsystem.specification.common.OrderBySpec;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StockSpec {
    public static Specification<Stock> filter(StockFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.storeId() != null) {
                predicates.add(cb.equal(root.get("stores").get("id"), filter.storeId()));
            }

            Join<Stock, Product> productJoin = null;
            Join<Product, Model> modelJoin = null;

            if (filter.productId() != null) {
                productJoin = root.join("product", JoinType.LEFT);
                modelJoin = productJoin.join("models", JoinType.LEFT);
                predicates.add(cb.or(
                        cb.equal(productJoin.get("id"), filter.productId()),
                        cb.equal(modelJoin.get("id"), filter.productId())
                ));
            }

            if (filter.modelId() != null) {
                if (productJoin == null) {
                    productJoin = root.join("product", JoinType.LEFT);
                }
                if (modelJoin == null) {
                    modelJoin = productJoin.join("models", JoinType.LEFT);
                }
                predicates.add(cb.equal(modelJoin.get("id"), filter.modelId()));
            }

            if (filter.brandId() != null) {
                if (productJoin == null) {
                    productJoin = root.join("product", JoinType.LEFT);
                }
                if (modelJoin == null) {
                    modelJoin = productJoin.join("models", JoinType.LEFT);
                }
                predicates.add(cb.equal(modelJoin.get("brand").get("id"), filter.brandId()));
            }

            if (filter.categoryId() != null) {
                if (productJoin == null) {
                    productJoin = root.join("product", JoinType.LEFT);
                }
                if (modelJoin == null) {
                    modelJoin = productJoin.join("models", JoinType.LEFT);
                }
                predicates.add(cb.equal(modelJoin.get("category").get("id"), filter.categoryId()));
            }

            if (filter.search() != null && !filter.search().isBlank()) {
                if (productJoin == null) {
                    productJoin = root.join("product", JoinType.LEFT);
                }
                if (modelJoin == null) {
                    modelJoin = productJoin.join("models", JoinType.LEFT);
                }
                Join<Stock, Stores> storeJoin = root.join("stores", JoinType.LEFT);
                String pattern = "%" + filter.search().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(productJoin.get("name")), pattern),
                        cb.like(cb.lower(modelJoin.get("name")), pattern),
                        cb.like(cb.lower(storeJoin.get("name")), pattern)
                ));
            }

            if (filter.status() != null && !filter.status().isBlank() && !"ALL".equalsIgnoreCase(filter.status().trim())) {
                String status = filter.status().trim().toUpperCase().replace("_", "");
                switch (status) {
                    case Constants.OUT_STOCK, "OUTOFSTOCK" -> predicates.add(cb.or(
                            cb.isNull(root.get("quantity")),
                            cb.lessThanOrEqualTo(root.get("quantity"), BigDecimal.ZERO)
                    ));
                    case Constants.LOW_STOCK, "LOW" -> predicates.add(cb.and(
                            cb.isNotNull(root.get("quantity")),
                            cb.greaterThan(root.get("quantity"), BigDecimal.ZERO),
                            cb.isNotNull(root.get("alertQuantity")),
                            cb.greaterThan(root.get("alertQuantity"), BigDecimal.ZERO),
                            cb.lessThanOrEqualTo(root.get("quantity"), root.get("alertQuantity"))
                    ));
                    case Constants.IN_STOCK, "INSTOCK" -> predicates.add(cb.and(
                            cb.isNotNull(root.get("quantity")),
                            cb.greaterThan(root.get("quantity"), BigDecimal.ZERO),
                            cb.or(
                                    cb.isNull(root.get("alertQuantity")),
                                    cb.lessThanOrEqualTo(root.get("alertQuantity"), BigDecimal.ZERO),
                                    cb.greaterThan(root.get("quantity"), root.get("alertQuantity"))
                            )
                    ));
                }
            }

            OrderBySpec.applyLatestUpdatedFirst(root, query, cb);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

