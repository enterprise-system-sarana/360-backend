package com.saranaresturantsystem.services.interfaces.catalog;

import com.saranaresturantsystem.dto.request.catalog.VariantTypeRequest;
import com.saranaresturantsystem.dto.response.catalog.VariantTypeResponse;
import com.saranaresturantsystem.entities.catalog.VariantType;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface VariantTypeService {
    Page<VariantTypeResponse> findAll(Map<String, String> params);

    VariantType findById(Long id);

    VariantTypeResponse save(VariantTypeRequest request);

    VariantTypeResponse update(Long id, VariantTypeRequest request);

    VariantTypeResponse delete(Long id);
}