package com.saranaresturantsystem.repository.catalog;

import com.saranaresturantsystem.entities.catalog.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryRepository extends JpaRepository<Category ,Long> , JpaSpecificationExecutor<Category> {
}
