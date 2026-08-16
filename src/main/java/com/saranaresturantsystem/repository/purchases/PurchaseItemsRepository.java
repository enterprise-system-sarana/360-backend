package com.saranaresturantsystem.repository.purchases;

import com.saranaresturantsystem.entities.purchase.PurchaseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseItemsRepository extends JpaRepository<PurchaseItem, Long>, JpaSpecificationExecutor<PurchaseItem> {
}
