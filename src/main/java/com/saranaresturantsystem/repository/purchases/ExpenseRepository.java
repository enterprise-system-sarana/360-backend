package com.saranaresturantsystem.repository.purchases;

import com.saranaresturantsystem.entities.purchase.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ExpenseRepository extends JpaRepository<Expenses, Long>, JpaSpecificationExecutor<Expenses> {

    @Query("SELECT SUM(e.amount) FROM Expenses e WHERE EXTRACT(YEAR FROM e.createdAt) = :year AND EXTRACT(MONTH FROM e.createdAt) = :month")
    BigDecimal getTotalExpensesForMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT EXTRACT(YEAR FROM e.createdAt) as year, EXTRACT(MONTH FROM e.createdAt) as month, SUM(e.amount) as total FROM Expenses e WHERE e.createdAt BETWEEN :startDate AND :endDate GROUP BY EXTRACT(YEAR FROM e.createdAt), EXTRACT(MONTH FROM e.createdAt) ORDER BY year ASC, month ASC")
    java.util.List<Object[]> getSalesExpenseByMonth(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);
}