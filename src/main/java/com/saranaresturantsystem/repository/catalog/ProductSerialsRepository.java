package com.saranaresturantsystem.repository.catalog;

import com.saranaresturantsystem.entities.catalog.ProductSerials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSerialsRepository extends JpaRepository<ProductSerials, Long>, JpaSpecificationExecutor<ProductSerials> {
}
