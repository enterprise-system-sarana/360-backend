package com.saranaresturantsystem.services.interfaces.catalog;

import com.saranaresturantsystem.dto.request.catalog.CategoryRequest;
import com.saranaresturantsystem.dto.response.catalog.CategoryResponse;
import com.saranaresturantsystem.entities.catalog.Category;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface CategoryService {
    Page<CategoryResponse> findAll(Map<String, String> params);

    CategoryResponse save(CategoryRequest request);

    CategoryResponse update(Long id, CategoryRequest request);

    CategoryResponse getById(Long id);

    void delete(Long id);

    Category findById(Long id);

}
