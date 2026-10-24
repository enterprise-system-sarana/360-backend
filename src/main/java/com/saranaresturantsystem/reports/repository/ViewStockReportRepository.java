package com.saranaresturantsystem.reports.repository;

import com.saranaresturantsystem.reports.dto.stock.StockReportSummaryProjection;
import com.saranaresturantsystem.reports.entities.ViewStockReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ViewStockReportRepository extends JpaRepository<ViewStockReport, Long>, JpaSpecificationExecutor<ViewStockReport> {

    @Query("""
        SELECT 
            COALESCE(SUM(v.currentQuantity), 0) AS totalQuantity,
            COALESCE(SUM(v.totalCostValue), 0) AS totalCostValue,
            COALESCE(SUM(v.totalSaleValue), 0) AS totalSaleValue,
            COALESCE(SUM(v.potentialProfit), 0) AS totalPotentialProfit,
            COUNT(v.stockId) AS totalItems,
            COALESCE(SUM(CASE WHEN v.stockStatus = 'IN_STOCK' THEN 1 ELSE 0 END), 0) AS inStockCount,
            COALESCE(SUM(CASE WHEN v.stockStatus = 'LOW_STOCK' THEN 1 ELSE 0 END), 0) AS lowStockCount,
            COALESCE(SUM(CASE WHEN v.stockStatus = 'OUT_OF_STOCK' THEN 1 ELSE 0 END), 0) AS outOfStockCount
        FROM ViewStockReport v
        WHERE (:storeId IS NULL OR v.storeId = :storeId)
          AND (:productId IS NULL OR v.productId = :productId)
          AND (:categoryId IS NULL OR v.categoryId = :categoryId)
          AND (:brandId IS NULL OR v.brandId = :brandId)
          AND (CAST(:stockStatus AS string) IS NULL OR UPPER(v.stockStatus) = UPPER(CAST(:stockStatus AS string)))
          AND (
                :lowStockOnly IS NULL OR :lowStockOnly = false OR v.stockStatus IN ('LOW_STOCK', 'OUT_OF_STOCK')
          )
          AND (CAST(:search AS string) IS NULL OR (
                LOWER(v.productName) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
                OR LOWER(v.productCode) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
                OR LOWER(v.storeName) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
          ))
    """)
    StockReportSummaryProjection getSummary(
            @Param("storeId") Long storeId,
            @Param("productId") Long productId,
            @Param("categoryId") Long categoryId,
            @Param("brandId") Long brandId,
            @Param("stockStatus") String stockStatus,
            @Param("lowStockOnly") Boolean lowStockOnly,
            @Param("search") String search
    );
}
