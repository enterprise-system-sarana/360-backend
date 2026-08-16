package com.saranaresturantsystem.repository.Inventory;

import com.saranaresturantsystem.entities.inventory.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long>, JpaSpecificationExecutor<Stock> {
    Optional<Stock> findByProductIdAndStoresId(
            Long productId, Long storeId);
}