package com.saranaresturantsystem.reports.service.customer;

import com.saranaresturantsystem.reports.dto.customers.AccountReceivableReportResponse;
import com.saranaresturantsystem.reports.specification.customers.AccountReceivableReportFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountReceivableReportService {
    Page<AccountReceivableReportResponse> getAccountReceivableReport(AccountReceivableReportFilter filter, Pageable pageable);
}