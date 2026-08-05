package com.saranaresturantsystem.repository.purchases;

import com.saranaresturantsystem.entities.purchase.Purchase_Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseItemsRepository extends JpaRepository<Purchase_Items, Long>, JpaSpecificationExecutor<Purchase_Items> {
}
