package com.saranaresturantsystem.services.interfaces.catalog;

import com.saranaresturantsystem.dto.request.catalog.VariantValueRequest;
import com.saranaresturantsystem.dto.response.catalog.VariantValueResponse;
import com.saranaresturantsystem.entities.catalog.VariantValue;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface VariantValueService {
    Page<VariantValueResponse> findAll(Map<String, String> params);

    VariantValue findById(Long id);

    VariantValueResponse save(VariantValueRequest request);

    VariantValueResponse update(Long id, VariantValueRequest request);

    VariantValueResponse delete(Long id);
}