package com.saranaresturantsystem.repository.Inventory;

import com.saranaresturantsystem.entities.inventory.Stock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long>, JpaSpecificationExecutor<Stock> {

    Optional<Stock> findByProductIdAndStoresId(Long productId, Long storeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Stock s WHERE s.product.id = :productId AND s.stores.id = :storeId")
    Optional<Stock> findByProductIdAndStoresIdForUpdate(
            @Param("productId") Long productId,
            @Param("storeId") Long storeId);

    List<Stock> findByProductId(Long productId);

    @Query("""
            SELECT s
            FROM Stock s
            WHERE s.quantity <= s.alertQuantity
              AND (:storeId IS NULL OR s.stores.id = :storeId)
              AND (:productId IS NULL OR s.product.id = :productId)
            """)
    Optional<Stock> findLowStock(@Param("storeId") Long storeId, @Param("productId") Long productId);
}