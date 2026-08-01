package com.saranaresturantsystem.repository.catalog;

import com.saranaresturantsystem.entities.catalog.VariantType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VariantTypeRepository extends JpaRepository<VariantType, Long>, JpaSpecificationExecutor<VariantType> {
}
