package com.saranaresturantsystem.repository.catalog;

import com.saranaresturantsystem.entities.catalog.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ModelRepository extends JpaRepository<Model, Long> , JpaSpecificationExecutor<Model> {
}
