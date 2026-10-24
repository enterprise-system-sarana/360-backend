package com.saranaresturantsystem.reports.repository;

import com.saranaresturantsystem.reports.dto.payment.PaymentReportSummaryProjection;
import com.saranaresturantsystem.reports.entities.ViewPurchasePaymentsReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ViewPurchasePaymentsReportRepository extends JpaRepository<ViewPurchasePaymentsReport, Long>, JpaSpecificationExecutor<ViewPurchasePaymentsReport> {

    @Query("""
        SELECT 
            COALESCE(SUM(v.totalAmount), 0) AS totalAmount,
            COALESCE(SUM(v.receivedAmount), 0) AS totalReceivedAmount,
            COALESCE(SUM(v.returnAmount), 0) AS totalReturnAmount,
            COALESCE(SUM(v.dueAmount), 0) AS totalDueAmount,
            COUNT(v.paymentId) AS totalTransactions
        FROM ViewPurchasePaymentsReport v
        WHERE (:storeId IS NULL OR v.storeId = :storeId)
          AND (:supplierId IS NULL OR v.supplierId = :supplierId)
          AND (:purchaseId IS NULL OR v.purchaseId = :purchaseId)
          AND (CAST(:paymentStatus AS string) IS NULL OR UPPER(v.paymentStatus) = UPPER(CAST(:paymentStatus AS string)))
          AND (CAST(:startDate AS timestamp) IS NULL OR v.paymentDate >= :startDate)
          AND (CAST(:endDate AS timestamp) IS NULL OR v.paymentDate <= :endDate)
    """)
    PaymentReportSummaryProjection getSummary(
            @Param("storeId") Long storeId,
            @Param("supplierId") Long supplierId,
            @Param("purchaseId") Long purchaseId,
            @Param("paymentStatus") String paymentStatus,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
