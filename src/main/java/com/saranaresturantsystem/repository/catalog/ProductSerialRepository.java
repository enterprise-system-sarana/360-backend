package com.saranaresturantsystem.repository.catalog;

import com.saranaresturantsystem.entities.catalog.ProductSerials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductSerialRepository extends JpaRepository<ProductSerials , Long>  , JpaSpecificationExecutor<ProductSerials> {
}
