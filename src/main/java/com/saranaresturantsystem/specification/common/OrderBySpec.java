package com.saranaresturantsystem.specification.common;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class OrderBySpec {

    /**
     * Orders query so that the most recently updated or created entities appear first.
     * Automatically bypassed for count queries (Long/long return types).
     */
    public static <T> void applyLatestUpdatedFirst(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (query == null) {
            return;
        }
        Class<?> resultType = query.getResultType();
        if (Long.class.equals(resultType) || long.class.equals(resultType)) {
            return;
        }

        try {
            query.orderBy(
                    cb.desc(cb.coalesce(root.get("updatedAt"), root.get("createdAt"))),
                    cb.desc(root.get("id"))
            );
        } catch (IllegalArgumentException e) {
            // Fallback for entities without updatedAt / createdAt
            try {
                query.orderBy(cb.desc(root.get("id")));
            } catch (Exception ignored) {
            }
        }
    }
}
