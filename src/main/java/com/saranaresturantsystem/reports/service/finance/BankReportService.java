package com.saranaresturantsystem.reports.service.finance;

import com.saranaresturantsystem.reports.dto.bank.BankReportFilter;
import com.saranaresturantsystem.reports.dto.bank.BankReportResponse;
import com.saranaresturantsystem.reports.dto.bank.BankReportSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BankReportService {
    BankReportSummaryResponse getBankReportSummary(BankReportFilter filter);
    Page<BankReportResponse> getBankReportPaged(BankReportFilter filter, Pageable pageable);
}

