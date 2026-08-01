package com.saranaresturantsystem.repository.purchases;

import com.saranaresturantsystem.entities.purchase.Suppliers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SupplierRepository extends JpaRepository<Suppliers , Long> , JpaSpecificationExecutor<Suppliers> {
}
