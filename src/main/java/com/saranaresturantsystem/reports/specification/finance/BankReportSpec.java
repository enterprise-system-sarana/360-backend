package com.saranaresturantsystem.reports.specification.finance;

import com.saranaresturantsystem.reports.dto.bank.BankReportFilter;
import com.saranaresturantsystem.reports.entities.ViewBankReport;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BankReportSpec {

    public static Specification<ViewBankReport> filter(BankReportFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter != null) {
                if (filter.startDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), filter.startDate().atStartOfDay()));
                }
                if (filter.endDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), filter.endDate().atTime(LocalTime.MAX)));
                }
                if (filter.bankId() != null) {
                    predicates.add(cb.equal(root.get("bankId"), filter.bankId()));
                }
                if (filter.transactionType() != null && !filter.transactionType().isBlank()) {
                    predicates.add(cb.equal(cb.upper(root.get("transactionType")), filter.transactionType().toUpperCase().trim()));
                }
                if (filter.flowType() != null && !filter.flowType().isBlank()) {
                    predicates.add(cb.equal(cb.upper(root.get("flowType")), filter.flowType().toUpperCase().trim()));
                }
                if (filter.search() != null && !filter.search().isBlank()) {
                    String pattern = "%" + filter.search().toLowerCase().trim() + "%";
                    predicates.add(cb.or(
                            cb.like(cb.lower(root.get("bankName")), pattern),
                            cb.like(cb.lower(root.get("accountName")), pattern),
                            cb.like(cb.lower(root.get("accountNumber")), pattern),
                            cb.like(cb.lower(root.get("transactionReference")), pattern),
                            cb.like(cb.lower(root.get("description")), pattern),
                            cb.like(cb.lower(cb.coalesce(root.get("saleNo"), "")), pattern),
                            cb.like(cb.lower(cb.coalesce(root.get("purchaseReferenceNo"), "")), pattern),
                            cb.like(cb.lower(cb.coalesce(root.get("expenseReference"), "")), pattern)
                    ));
                }
            }

            if (query != null) {
                query.orderBy(cb.desc(root.get("transactionDate")), cb.desc(root.get("transactionId")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

