package com.saranaresturantsystem.repository.catalog;

import com.saranaresturantsystem.entities.catalog.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product , Long> , JpaSpecificationExecutor<Product> {
}
