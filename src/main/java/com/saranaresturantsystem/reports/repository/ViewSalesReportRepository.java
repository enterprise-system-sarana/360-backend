package com.saranaresturantsystem.reports.repository;

import com.saranaresturantsystem.reports.dto.sales.SalesReportSummaryProjection;
import com.saranaresturantsystem.reports.entities.ViewSalesReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ViewSalesReportRepository extends JpaRepository<ViewSalesReport, Long>, JpaSpecificationExecutor<ViewSalesReport> {

    @Query("""
        SELECT 
            COALESCE(SUM(v.grandTotal), 0) AS totalSalesAmount,
            COALESCE(SUM(v.discount), 0) AS totalDiscount,
            COALESCE(SUM(v.paidAmount), 0) AS totalPaidAmount,
            COALESCE(SUM(v.totalCost), 0) AS totalCost,
            COALESCE(SUM(v.totalProfit), 0) AS totalProfit,
            COUNT(v.saleId) AS totalTransactions
        FROM ViewSalesReport v
        WHERE (:storeId IS NULL OR v.storeId = :storeId)
          AND (:customerId IS NULL OR v.customerId = :customerId)
          AND (CAST(:saleStatus AS string) IS NULL OR UPPER(v.saleStatus) = UPPER(CAST(:saleStatus AS string)))
          AND (CAST(:paymentStatus AS string) IS NULL OR UPPER(v.paymentStatus) = UPPER(CAST(:paymentStatus AS string)))
          AND (CAST(:startDate AS timestamp) IS NULL OR v.saleDate >= :startDate)
          AND (CAST(:endDate AS timestamp) IS NULL OR v.saleDate <= :endDate)
    """)
    SalesReportSummaryProjection getSummary(
            @Param("storeId") Long storeId,
            @Param("customerId") Long customerId,
            @Param("saleStatus") String saleStatus,
            @Param("paymentStatus") String paymentStatus,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
