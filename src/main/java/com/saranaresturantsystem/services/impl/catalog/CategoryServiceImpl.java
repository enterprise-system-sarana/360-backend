
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
import com.saranaresturantsystem.specification.catalog.category.CategorySpec;
import com.saranaresturantsystem.utils.PageUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UniqueChecker uniqueChecker;
    private final ObjectMapper objectMapper;
    private final CategoryMapper categoryMapper;

    /**
     * Find all categories with dynamic filtering and pagination.
     * No cache because filters and pagination generate many cache keys.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> findAll(Map<String, String> params) {

        CategoryFilter categoryFilter =
                objectMapper.convertValue(params, CategoryFilter.class);

        Pageable pageable = PageUtil.fromParams(params);

        Specification<Category> spec =
                CategorySpec.filterBy(categoryFilter);

        return categoryRepository.findAll(spec, pageable)
                .map(categoryMapper::toResponse);
    }

    /**
     * Create a new category.
     * Evict all category cache entries after successful method execution.
     */
    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse save(CategoryRequest request) {

        Category category = categoryMapper.toEntity(request);

        uniqueChecker.verify(
                categoryRepository,
                category,
                "name",
                category.getName()
        );

        uniqueChecker.verify(
                categoryRepository,
                category,
                "code",
                category.getCode()
        );

        category.setStatus(Constants.STATUS_ACTIVE);

        Category savedCategory = categoryRepository.save(category);

        log.info("Category created successfully with ID: {}", savedCategory.getId());

        return categoryMapper.toResponse(savedCategory);
    }

    /**
     * Update an existing category.
     * Evict all category cache entries.
     */
    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse update(Long id, CategoryRequest request) {

        log.info("Updating Category with ID: {}", id);

        Category category = findById(id);

        uniqueChecker.verify(
                categoryRepository,
                category,
                "name",
                request.name()
        );

        uniqueChecker.verify(
                categoryRepository,
                category,
                "code",
                request.code()
        );

        categoryMapper.updateEntityFromRequest(request, category);
        if (request.status() != null && !request.status().isBlank()) {
            category.setStatus(request.status().trim().toUpperCase());
        }

        Category savedCategory = categoryRepository.save(category);

        log.info("Category updated successfully with ID: {}", id);

        return categoryMapper.toResponse(savedCategory);
    }

    /**
     * Get category by ID.
     * Cache the response using category ID as cache key.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#id")
    public CategoryResponse getById(Long id) {

        log.info("Fetching Category from database (Cache Miss) for ID: {}", id);

        Category category = findById(id);

        return categoryMapper.toResponse(category);
    }

    /**
     * Soft delete category.
     * Evict all category cache entries.
     */
    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void delete(Long id) {

        log.info("Deleting Category with ID: {}", id);

        Category category = findById(id);

        category.setStatus(Constants.STATUS_INACTIVE);

        categoryRepository.save(category);

        log.info("Category marked inactive successfully with ID: {}", id);
    }

    /**
     * Find category by ID.
     * Exclude INIT and DELETE statuses.
     */
    @Override
    @Transactional(readOnly = true)
    public Category findById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category : " + id)
                );

        if (Constants.STATUS_INIT.equals(category.getStatus())) {

            throw new ResourceNotFoundException("Category : " + id);
        }

        return category;
    }
}
