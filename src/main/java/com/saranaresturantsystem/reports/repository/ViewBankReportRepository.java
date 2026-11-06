package com.saranaresturantsystem.reports.repository;

import com.saranaresturantsystem.reports.dto.bank.BankReportSummaryProjection;
import com.saranaresturantsystem.reports.entities.ViewBankReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ViewBankReportRepository extends JpaRepository<ViewBankReport, Long>, JpaSpecificationExecutor<ViewBankReport> {

    @Query("""
        SELECT 
            COALESCE(SUM(CASE WHEN v.flowType = 'IN' THEN v.amount ELSE 0 END), 0) AS totalInflow,
            COALESCE(SUM(CASE WHEN v.flowType = 'OUT' THEN v.amount ELSE 0 END), 0) AS totalOutflow,
            COUNT(v.transactionId) AS totalTransactions
        FROM ViewBankReport v
        WHERE (:bankId IS NULL OR v.bankId = :bankId)
          AND (CAST(:transactionType AS string) IS NULL OR UPPER(v.transactionType) = UPPER(CAST(:transactionType AS string)))
          AND (CAST(:flowType AS string) IS NULL OR UPPER(v.flowType) = UPPER(CAST(:flowType AS string)))
          AND (CAST(:startDate AS timestamp) IS NULL OR v.transactionDate >= :startDate)
          AND (CAST(:endDate AS timestamp) IS NULL OR v.transactionDate <= :endDate)
    """)
    BankReportSummaryProjection getSummary(
            @Param("bankId") Long bankId,
            @Param("transactionType") String transactionType,
            @Param("flowType") String flowType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}

