package com.saranaresturantsystem.services.impl.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.common.UniqueChecker;
import com.saranaresturantsystem.constants.Constants;
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


import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import java.util.Map;

import static com.saranaresturantsystem.constants.Constants.STATUS_DELETE;
import static com.saranaresturantsystem.constants.Constants.STATUS_INIT;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final UniqueChecker uniqueChecker;
    private final ObjectMapper objectMapper;
    private final BrandMapper brandMappers;

    @Override
    @Transactional(readOnly = true)
    public Page<BrandResponse> findAll(Map<String, String> params) {
        BrandFilter filter = objectMapper.convertValue(params, BrandFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Brand> spec = BrandSpec.filterBy(filter);
        return brandRepository.findAll(spec, pageable).map(brandMappers::toResponse);
    }

//    @Cacheable(value = "brands", key = "#id")
    @Override
    public Brand findById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand " + id));

        if (brand.getStatus().equals(STATUS_DELETE) || brand.getStatus().equals(STATUS_INIT)) {
            throw new ResourceNotFoundException("Brand " + id);
        }
        return brand;
    }

    @Override
    public BrandResponse getById(Long id) {
        return  brandMappers.toResponse(findById(id));
    }

    @Override
    @Transactional
    public BrandResponse save(BrandRequest request) {
        Brand brand = brandMappers.toEntity(request);
        uniqueChecker.verify(brandRepository, brand, "name", brand.getName());
        brand.setStatus(Constants.STATUS_ACTIVE);
        Brand savedBrand = brandRepository.save(brand);
        return brandMappers.toResponse(savedBrand);
    }

//    @CacheEvict(value = "brands", key = "#id")
    @Override
    @Transactional
    public BrandResponse update(Long id, BrandRequest request) {
        Brand brand = findById(id);
        uniqueChecker.verify(brandRepository, brand, "name", request.name());
        brandMappers.updateEntityFromRequest(request, brand);
        Brand updatedBrand = brandRepository.save(brand);
        return brandMappers.toResponse(updatedBrand);
    }

//    @CacheEvict(value = "brands", key = "#id")
    @Override
    @Transactional
    public BrandResponse delete(Long id) {
        Brand brand = findById(id);
        brand.setStatus(STATUS_DELETE);
        Brand deletedBrand = brandRepository.save(brand);
        return brandMappers.toResponse(deletedBrand);
    }
}