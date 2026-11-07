package com.saranaresturantsystem.specification.common;

import com.saranaresturantsystem.constants.Constants;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class StatusSpec {

    /**
     * Applies status filtering to criteria query:
     * - If specific status is provided in filter (e.g., "ACT", "INA", "DEL"), match it.
     * - If "ALL" is provided, show all non-deleted records (exclude DEL and INT).
     * - If status is not provided (null / blank), default to showing ONLY ACTIVE ("ACT") records.
     */
    public static <T> Predicate filterStatus(Root<T> root, CriteriaBuilder cb, String status) {
        if (status != null && !status.isBlank()) {
            String trimmed = status.trim().toUpperCase();
            if ("ALL".equals(trimmed)) {
                return cb.not(root.get("status").in(Constants.STATUS_DELETE, Constants.STATUS_INIT, "DELETE", "DELETED"));
            }
            if ("ACTIVE".equals(trimmed) || Constants.STATUS_ACTIVE.equals(trimmed)) {
                return cb.or(
                        cb.equal(cb.upper(root.get("status")), Constants.STATUS_ACTIVE),
                        cb.equal(cb.upper(root.get("status")), "ACTIVE")
                );
            }
            if ("INACTIVE".equals(trimmed) || Constants.STATUS_INACTIVE.equals(trimmed)) {
                return cb.or(
                        cb.equal(cb.upper(root.get("status")), Constants.STATUS_INACTIVE),
                        cb.equal(cb.upper(root.get("status")), "INACTIVE"),
                        cb.equal(cb.upper(root.get("status")), Constants.STATUS_DELETE),
                        cb.equal(cb.upper(root.get("status")), "DELETE"),
                        cb.equal(cb.upper(root.get("status")), "DELETED")
                );
            }
            if ("DELETE".equals(trimmed) || "DELETED".equals(trimmed) || Constants.STATUS_DELETE.equals(trimmed)) {
                return cb.or(
                        cb.equal(cb.upper(root.get("status")), Constants.STATUS_DELETE),
                        cb.equal(cb.upper(root.get("status")), "DELETE"),
                        cb.equal(cb.upper(root.get("status")), "DELETED")
                );
            }
            return cb.equal(cb.upper(root.get("status")), trimmed);
        }
        // Default when no status or blank is specified: ONLY ACTIVE! ("when show2 only act don't show inc")
        return cb.or(
                cb.equal(cb.upper(root.get("status")), Constants.STATUS_ACTIVE),
                cb.equal(cb.upper(root.get("status")), "ACTIVE")
        );
    }
}
