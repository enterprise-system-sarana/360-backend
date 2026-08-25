package com.saranaresturantsystem.specification.common;

import com.saranaresturantsystem.constants.Constants;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class StatusSpec {

    /**
     * Applies status filtering to criteria query:
     * - If specific status is provided in filter (e.g., "ACT", "INT", "DEL"), match it.
     * - If status is not provided (null / blank), hide deleted (DEL) and inactive (INT) records.
     */
    public static <T> Predicate filterStatus(Root<T> root, CriteriaBuilder cb, String status) {
        if (status != null && !status.isBlank()) {
            return cb.equal(cb.upper(root.get("status")), status.trim().toUpperCase());
        }
        return cb.not(root.get("status").in(Constants.STATUS_DELETE, Constants.STATUS_INIT));
    }
}
