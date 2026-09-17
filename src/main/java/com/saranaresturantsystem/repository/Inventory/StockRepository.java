package com.saranaresturantsystem.repository.Inventory;

import com.saranaresturantsystem.dto.response.inventory.StockResponse;
import com.saranaresturantsystem.entities.inventory.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long>, JpaSpecificationExecutor<Stock> {
    Optional<Stock> findByProductIdAndStoresId(
            Long productId, Long storeId);

    List<Stock> findByProductId(Long productId);

    @Query("""
                SELECT s
                FROM Stock s
                WHERE s.quantity <= s.alertQuantity
                  AND (:storeId IS NULL OR s.stores.id = :storeId)
            """)
    Optional<Stock> findLowStock(@Param("storeId") Long storeId, Long productId);

//    Optional<StockResponse>
}