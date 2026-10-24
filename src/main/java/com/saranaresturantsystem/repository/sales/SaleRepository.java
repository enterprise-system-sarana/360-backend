package com.saranaresturantsystem.repository.sales;

import com.saranaresturantsystem.entities.sales.Sales;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sales, Long>, JpaSpecificationExecutor<Sales> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Sales s WHERE s.id = :id")
    Optional<Sales> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT EXTRACT(YEAR FROM s.date) as year, EXTRACT(MONTH FROM s.date) as month, SUM(s.grandTotal) as total FROM Sales s WHERE s.date BETWEEN :startDate AND :endDate GROUP BY EXTRACT(YEAR FROM s.date), EXTRACT(MONTH FROM s.date) ORDER BY year ASC, month ASC")
    List<Object[]> getSalesIncomeByMonth(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT EXTRACT(YEAR FROM s.date) as year, EXTRACT(MONTH FROM s.date) as month, SUM(s.returnAmount) as total FROM Sales s WHERE s.date BETWEEN :startDate AND :endDate GROUP BY EXTRACT(YEAR FROM s.date), EXTRACT(MONTH FROM s.date) ORDER BY year ASC, month ASC")
    List<Object[]> getSalesReturnByMonth(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
