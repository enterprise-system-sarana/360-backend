package com.saranaresturantsystem.repository.purchases;

import com.saranaresturantsystem.entities.purchase.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchasesRepository extends JpaRepository<Purchase, Long> , JpaSpecificationExecutor<Purchase> {
}
