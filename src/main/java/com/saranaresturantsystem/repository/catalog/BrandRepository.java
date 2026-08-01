package com.saranaresturantsystem.repository.catalog;

import com.saranaresturantsystem.entities.catalog.Brands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BrandRepository extends JpaRepository<Brands, Long>, JpaSpecificationExecutor<Brands> {
}
