package com.saranaresturantsystem.repository.catalog;

import com.saranaresturantsystem.entities.catalog.VariantType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface VariantTypeRepository extends JpaRepository<VariantType, Long>, JpaSpecificationExecutor<VariantType> {

    @Override
    @EntityGraph(attributePaths = {"variantValues"})
    @NonNull
    Page<VariantType> findAll(Specification<VariantType> spec, @NonNull Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"variantValues"})
    @NonNull
    Optional<VariantType> findById(@NonNull Long id);
}

