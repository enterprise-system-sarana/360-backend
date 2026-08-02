package com.saranaresturantsystem.services.impl.catalog;

import com.saranaresturantsystem.common.UniqueChecker;
import com.saranaresturantsystem.dto.request.catalog.BrandRequest;
import com.saranaresturantsystem.dto.response.catalog.BrandResponse;
import com.saranaresturantsystem.entities.catalog.Brand;
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
        Specification<Brand> spec = BrandSpec.filterBy(filter);
        return brandRepository.findAll(spec, pageable).map(brandMappers::toResponse);
    }

    @Override
    public Brand findById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));

        if (!"ACTIVE".equals(brand.getStatus())) {
            throw new ResourceNotFoundException("Brand is inactive with id: " + id);
        }
        return brand;
    }

    @Override
    @Transactional
    public BrandResponse save(BrandRequest request) {
        Brand brand = brandMappers.toEntity(request);
        uniqueChecker.verify(brandRepository, brand, "name", brand.getName());
        brand.setStatus("ACTIVE");
        Brand savedBrand = brandRepository.save(brand);
        return brandMappers.toResponse(savedBrand);
    }

    @Override
    @Transactional
    public BrandResponse update(Long id, BrandRequest request) {
        Brand brand = findById(id);
        brandMappers.updateEntityFromRequest(request, brand);
        Brand updatedBrand = brandRepository.save(brand);
        return brandMappers.toResponse(updatedBrand);
    }

    @Override
    @Transactional
    public BrandResponse delete(Long id) {
        Brand brand = findById(id);
        brand.setStatus("INACTIVE");
        Brand deletedBrand = brandRepository.save(brand);
        return brandMappers.toResponse(deletedBrand);
    }
}