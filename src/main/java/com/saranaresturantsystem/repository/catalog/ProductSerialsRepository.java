package com.saranaresturantsystem.repository.catalog;

import com.saranaresturantsystem.entities.catalog.ProductSerials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;

@Repository
public interface ProductSerialsRepository extends JpaRepository<ProductSerials, Long>, JpaSpecificationExecutor<ProductSerials> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<ProductSerials> findByProductIdAndStoreIdAndStatusOrderByIdAsc(
            Long productId, Long storeId, String status);

    List<ProductSerials> findByPurchaseId(Long purchaseId);

    boolean existsByBarcode(String barcode);
}
