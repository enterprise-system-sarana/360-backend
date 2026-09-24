package com.saranaresturantsystem.services.interfaces.reports;

import com.saranaresturantsystem.dto.response.finance.ProfitLossReportResponse;

import java.time.LocalDate;

public interface ProfitLossReportService {
    ProfitLossReportResponse generateProfitLossReport(LocalDate startDate, LocalDate endDate, Integer months);
}
