package com.saranaresturantsystem.reports.repository;

import com.saranaresturantsystem.reports.dto.purchases.PurchaseReportSummaryProjection;
import com.saranaresturantsystem.reports.entities.ViewPurchaseReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ViewPurchaseReportRepository extends JpaRepository<ViewPurchaseReport, Long>, JpaSpecificationExecutor<ViewPurchaseReport> {

    @Query("""
        SELECT 
            COALESCE(SUM(v.grandTotal), 0) AS totalAmount,
            COALESCE(SUM(v.discount), 0) AS totalDiscount,
            COALESCE(SUM(v.paidAmount), 0) AS totalPaidAmount,
            COALESCE(SUM(v.dueAmount), 0) AS totalDueAmount,
            COALESCE(SUM(v.totalQuantity), 0) AS totalQuantity,
            COUNT(v.purchaseId) AS totalTransactions
        FROM ViewPurchaseReport v
        WHERE (:storeId IS NULL OR v.storeId = :storeId)
          AND (:supplierId IS NULL OR v.supplierId = :supplierId)
          AND (CAST(:status AS string) IS NULL OR UPPER(v.status) = UPPER(CAST(:status AS string)))
          AND (CAST(:paymentStatus AS string) IS NULL OR UPPER(v.paymentStatus) = UPPER(CAST(:paymentStatus AS string)))
          AND (CAST(:startDate AS date) IS NULL OR v.purchaseDate >= :startDate)
          AND (CAST(:endDate AS date) IS NULL OR v.purchaseDate <= :endDate)
    """)
    PurchaseReportSummaryProjection getSummary(
            @Param("storeId") Long storeId,
            @Param("supplierId") Long supplierId,
            @Param("status") String status,
            @Param("paymentStatus") String paymentStatus,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
