package com.saranaresturantsystem.reports.repository;

import com.saranaresturantsystem.reports.dto.payment.PaymentReportSummaryProjection;
import com.saranaresturantsystem.reports.entities.ViewSalePaymentsReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ViewSalePaymentsReportRepository extends JpaRepository<ViewSalePaymentsReport, Long>, JpaSpecificationExecutor<ViewSalePaymentsReport> {

    @Query("""
        SELECT 
            COALESCE(SUM(v.totalAmount), 0) AS totalAmount,
            COALESCE(SUM(v.receivedAmount), 0) AS totalReceivedAmount,
            COALESCE(SUM(v.returnAmount), 0) AS totalReturnAmount,
            COALESCE(SUM(v.dueAmount), 0) AS totalDueAmount,
            COUNT(v.paymentId) AS totalTransactions
        FROM ViewSalePaymentsReport v
        WHERE (:storeId IS NULL OR v.storeId = :storeId)
          AND (:customerId IS NULL OR v.customerId = :customerId)
          AND (:saleId IS NULL OR v.saleId = :saleId)
          AND (CAST(:paymentStatus AS string) IS NULL OR UPPER(v.paymentStatus) = UPPER(CAST(:paymentStatus AS string)))
          AND (CAST(:startDate AS timestamp) IS NULL OR v.paymentDate >= :startDate)
          AND (CAST(:endDate AS timestamp) IS NULL OR v.paymentDate <= :endDate)
    """)
    PaymentReportSummaryProjection getSummary(
            @Param("storeId") Long storeId,
            @Param("customerId") Long customerId,
            @Param("saleId") Long saleId,
            @Param("paymentStatus") String paymentStatus,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
