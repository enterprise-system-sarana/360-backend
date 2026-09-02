package com.saranaresturantsystem.services.interfaces.catalog;

import com.saranaresturantsystem.dto.request.catalog.BrandRequest;
import com.saranaresturantsystem.dto.response.catalog.BrandResponse;
import com.saranaresturantsystem.entities.catalog.Brand;

import org.springframework.data.domain.Page;

import java.util.Map;

public interface BrandService {
    Page<BrandResponse> findAll(Map<String, String> params);

    Brand findById(Long id);

    BrandResponse getById(Long id);
    BrandResponse save(BrandRequest request);

    BrandResponse update(Long id, BrandRequest request);

    BrandResponse delete(Long id);
}
