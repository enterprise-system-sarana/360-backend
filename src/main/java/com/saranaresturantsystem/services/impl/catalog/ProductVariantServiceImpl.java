package com.saranaresturantsystem.services.impl.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.dto.request.catalog.ProductVariantRequest;
import com.saranaresturantsystem.dto.response.catalog.ProductVariantResponse;
import com.saranaresturantsystem.entities.catalog.Product;
import com.saranaresturantsystem.entities.catalog.ProductVariant;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.catalog.ProductVariantMapper;
import com.saranaresturantsystem.repository.catalog.ProductVariantRepository;
import com.saranaresturantsystem.services.interfaces.catalog.ProductVariantService;
import com.saranaresturantsystem.specification.catalog.productVariant.ProductVariantFilter;
import com.saranaresturantsystem.specification.catalog.productVariant.ProductVariantSpec;
import com.saranaresturantsystem.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductVariantServiceImpl implements ProductVariantService {
    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantMapper variantMapper;
    private final ObjectMapper objectMapper;

    @Cacheable(value = "product_variants", key = "'all'")
    @Override
    public Page<ProductVariantResponse> findAll(Map<String, String> params) {
        ProductVariantFilter filter = objectMapper.convertValue(params, ProductVariantFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<ProductVariant> spec = ProductVariantSpec.filterBy(filter);
        return productVariantRepository.findAll(spec, pageable).map(variantMapper::toResponse);
    }

    @Cacheable(value = "product_variants", key = "#id")
    @Override
    public ProductVariant findById(Long id) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product  " + id));
        if (variant.getStatus().equals("INACTIVE")) {
            throw new ResourceNotFoundException("Product", id);
        }
        return variant;
    }

    @Cacheable(value = "product_variants", key = "#id")
    @Override
    public ProductVariantResponse getById(Long id) {
        ProductVariant variant = findById(id);
        return variantMapper.toResponse(variant);
    }

    @Override
    public ProductVariantResponse create(ProductVariantRequest request) {
        ProductVariant variant = variantMapper.toEntity(request);
        variant.setStatus("ACTIVE");
        ProductVariant save = productVariantRepository.save(variant);
        return variantMapper.toResponse(save);
    }

    @CacheEvict(value = "product_variants", key = "#id")
    @Override
    public ProductVariantResponse update(Long id, ProductVariantRequest request) {
        ProductVariant existingId = findById(id);
        variantMapper.updateFromRequest(request, existingId);
        return variantMapper.toResponse(productVariantRepository.save(existingId));
    }

    @CacheEvict(value = "product_variants", key = "#id")
    @Override
    public void delete(Long id) {
        ProductVariant variant = findById(id);
        if (variant.getStatus().equals("INACTIVE")) {
            throw new ResourceNotFoundException("variant", id);
        }
    }
}
