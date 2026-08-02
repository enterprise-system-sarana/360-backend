package com.saranaresturantsystem.services.impl.catalog;

import com.saranaresturantsystem.common.UniqueChecker;
import com.saranaresturantsystem.dto.request.catalog.BrandRequest;
import com.saranaresturantsystem.dto.response.catalog.BrandResponse;
import com.saranaresturantsystem.entities.catalog.Brands;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.catalog.BrandMapper;
import com.saranaresturantsystem.repository.catalog.BrandRepository;
import com.saranaresturantsystem.services.interfaces.catalog.BrandService;
import com.saranaresturantsystem.specification.catalog.brand.BrandFilter;
import com.saranaresturantsystem.specification.catalog.brand.BrandSpec;
import com.saranaresturantsystem.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final UniqueChecker uniqueChecker;
    private final ObjectMapper objectMapper;
    private final BrandMapper brandMappers;

    @Transactional(readOnly = true)
    @Override
    public Page<BrandResponse> findAll(Map<String, String> params) {
        BrandFilter filter = objectMapper.convertValue(params, BrandFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Brands> spec = BrandSpec.filterBy(filter);
        return brandRepository.findAll(spec, pageable).map(brandMappers::toResponse);
    }

    @Override
    public Brands findById(Long id) {
        Brands brands = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));

        if (!"ACTIVE".equals(brands.getStatus())) {
            throw new ResourceNotFoundException("Brand is inactive with id: " + id);
        }
        return brands;
    }

    @Override
    @Transactional
    public BrandResponse save(BrandRequest request) {
        Brands brands = brandMappers.toEntity(request);
        uniqueChecker.verify(brandRepository, brands, "name", brands.getName());
        brands.setStatus("ACTIVE");
        Brands savedBrand = brandRepository.save(brands);
        return brandMappers.toResponse(savedBrand);
    }

    @Override
    @Transactional
    public BrandResponse update(Long id, BrandRequest request) {
        Brands brands = findById(id);
        brandMappers.updateEntityFromRequest(request, brands);
        Brands updatedBrand = brandRepository.save(brands);
        return brandMappers.toResponse(updatedBrand);
    }

    @Override
    @Transactional
    public BrandResponse delete(Long id) {
        Brands brands = findById(id);
        brands.setStatus("INACTIVE");
        Brands deletedBrand = brandRepository.save(brands);
        return brandMappers.toResponse(deletedBrand);
    }
}