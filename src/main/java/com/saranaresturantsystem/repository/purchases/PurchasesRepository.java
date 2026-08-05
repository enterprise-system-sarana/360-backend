package com.saranaresturantsystem.repository.purchases;

import com.saranaresturantsystem.entities.purchase.Purchases;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchasesRepository extends JpaRepository<Purchases, Long> , JpaSpecificationExecutor<Purchases> {
}
