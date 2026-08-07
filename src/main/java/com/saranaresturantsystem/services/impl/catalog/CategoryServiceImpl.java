package com.saranaresturantsystem.services.impl.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.common.UniqueChecker;
import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.dto.request.catalog.CategoryRequest;
import com.saranaresturantsystem.dto.response.catalog.CategoryResponse;
import com.saranaresturantsystem.entities.catalog.Category;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.catalog.CategoryMapper;
import com.saranaresturantsystem.repository.catalog.CategoryRepository;
import com.saranaresturantsystem.services.interfaces.catalog.CategoryService;
import com.saranaresturantsystem.specification.catalog.category.CategoryFilter;
import com.saranaresturantsystem.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.saranaresturantsystem.specification.catalog.category.CategorySpec;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final UniqueChecker uniqueChecker;
    private final ObjectMapper objectMapper;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public Page<CategoryResponse> findAll(Map<String, String> params) {
        CategoryFilter categoryFilter = objectMapper.convertValue(params, CategoryFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Category> spec = CategorySpec.filterBy(categoryFilter);
        return categoryRepository.findAll(spec, pageable).map(categoryMapper::toResponse);
    }

    @Override
    @Transactional
    public CategoryResponse save(CategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        uniqueChecker.verify(categoryRepository, category, "name", category.getName());
        uniqueChecker.verify(categoryRepository, category, "code", category.getCode());
        category.setStatus(Constants.STATUS_ACTIVE);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);

    }

    @CacheEvict(value = "categories", key = "#id")
    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category exitingId = findById(id);

        categoryMapper.updateEntityFromRequest(request, exitingId);
        Category save = categoryRepository.save(exitingId);

        return categoryMapper.toResponse(save);
    }

    @Cacheable(value = "categories", key = "#id")
    @Override
    public CategoryResponse getById(Long id) {
        Category exitingCategory = findById(id);
        return categoryMapper.toResponse(exitingCategory);
    }

    @CacheEvict(value = "categories", key = "#id")
    @Override
    public void delete(Long id) {
        Category category = findById(id);
        category.setStatus(Constants.STATUS_DELETE);
        categoryRepository.save(category);
    }

    @Cacheable(value = "categories", key = "#id")
    @Override
    public Category findById(Long id) {
        Category findCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category : " + id));
        if (findCategory.getStatus().equals(Constants.STATUS_INIT) || findCategory.getStatus().equals(Constants.STATUS_DELETE)) {
            throw new ResourceNotFoundException("Category : " + id);
        }
        return findCategory;
    }
}
