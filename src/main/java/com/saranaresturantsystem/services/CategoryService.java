package com.saranaresturantsystem.services;

import com.saranaresturantsystem.dto.request.CategoryRequest;
import com.saranaresturantsystem.dto.response.CategoryResponse;
import com.saranaresturantsystem.entities.Category;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface CategoryService {
    Page<CategoryResponse> findAll(Map<String ,String> params);
    CategoryResponse save(CategoryRequest request);
    CategoryResponse update(Long id, CategoryRequest request);
    CategoryResponse getById(Long id);
    void delete(Long id);
    Category findById(Long id);

}
