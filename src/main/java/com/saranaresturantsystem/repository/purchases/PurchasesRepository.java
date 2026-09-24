package com.saranaresturantsystem.repository.purchases;

import com.saranaresturantsystem.entities.purchase.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchasesRepository extends JpaRepository<Purchase, Long> , JpaSpecificationExecutor<Purchase> {
    
    @Query("SELECT EXTRACT(YEAR FROM p.purchaseDate) as year, EXTRACT(MONTH FROM p.purchaseDate) as month, SUM(p.grandTotal) as total FROM Purchase p WHERE p.purchaseDate BETWEEN :startDate AND :endDate GROUP BY EXTRACT(YEAR FROM p.purchaseDate), EXTRACT(MONTH FROM p.purchaseDate) ORDER BY year ASC, month ASC")
    java.util.List<Object[]> getPurchaseExpenseByMonth(@Param("startDate") java.time.LocalDate startDate, @Param("endDate") java.time.LocalDate endDate);
}
