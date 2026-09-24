package com.saranaresturantsystem.repository.sales;

import com.saranaresturantsystem.entities.sales.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sales,Long>, JpaSpecificationExecutor<Sales> {

    @Query("SELECT SUM(s.grandTotal) FROM Sales s WHERE EXTRACT(YEAR FROM s.date) = :year AND EXTRACT(MONTH FROM s.date) = :month")
    Double getTotalRevenueForMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(s) FROM Sales s WHERE EXTRACT(YEAR FROM s.date) = :year AND EXTRACT(MONTH FROM s.date) = :month")
    Long getTotalSalesCountForMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(DISTINCT s.customer.id) FROM Sales s WHERE s.date >= :since")
    Long getActiveCustomersSince(@Param("since") LocalDateTime since);

    @Query("SELECT EXTRACT(MONTH FROM s.date) as month, SUM(s.grandTotal) as total FROM Sales s WHERE s.date >= :since GROUP BY EXTRACT(YEAR FROM s.date), EXTRACT(MONTH FROM s.date) ORDER BY EXTRACT(YEAR FROM s.date) ASC, EXTRACT(MONTH FROM s.date) ASC")
    List<Object[]> getMonthlyRevenueSince(@Param("since") LocalDateTime since);

    @Query("SELECT SUM((si.price - p.costPrice) * si.quantity) FROM SaleItems si JOIN si.product p JOIN si.sales s WHERE EXTRACT(YEAR FROM s.date) = :year AND EXTRACT(MONTH FROM s.date) = :month")
    BigDecimal getGrossProfitForMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT EXTRACT(YEAR FROM s.date) as year, EXTRACT(MONTH FROM s.date) as month, SUM(s.grandTotal) as total FROM Sales s WHERE s.date BETWEEN :startDate AND :endDate GROUP BY EXTRACT(YEAR FROM s.date), EXTRACT(MONTH FROM s.date) ORDER BY year ASC, month ASC")
    List<Object[]> getSalesIncomeByMonth(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT EXTRACT(YEAR FROM s.date) as year, EXTRACT(MONTH FROM s.date) as month, SUM(s.returnAmount) as total FROM Sales s WHERE s.date BETWEEN :startDate AND :endDate GROUP BY EXTRACT(YEAR FROM s.date), EXTRACT(MONTH FROM s.date) ORDER BY year ASC, month ASC")
    List<Object[]> getSalesReturnByMonth(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
