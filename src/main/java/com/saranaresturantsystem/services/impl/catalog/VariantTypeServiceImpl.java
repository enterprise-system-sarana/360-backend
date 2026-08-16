package com.saranaresturantsystem.services.impl.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.common.UniqueChecker;
import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.dto.request.catalog.VariantTypeRequest;
import com.saranaresturantsystem.dto.response.catalog.VariantTypeResponse;
import com.saranaresturantsystem.entities.catalog.VariantType;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.catalog.VariantTypeMapper;
import com.saranaresturantsystem.repository.catalog.VariantTypeRepository;
import com.saranaresturantsystem.services.interfaces.catalog.VariantTypeService;
import com.saranaresturantsystem.specification.catalog.varianttype.VariantTypeFilter;
import com.saranaresturantsystem.specification.catalog.varianttype.VariantTypeSpec;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class VariantTypeServiceImpl implements VariantTypeService {
    private final VariantTypeRepository variantTypeRepository;
    private final UniqueChecker uniqueChecker;
    private final ObjectMapper objectMapper;
    private final VariantTypeMapper variantTypeMapper;

    @Transactional(readOnly = true)
    @Override
    public Page<VariantTypeResponse> findAll(Map<String, String> params) {
        VariantTypeFilter filter = objectMapper.convertValue(params, VariantTypeFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<VariantType> spec = VariantTypeSpec.filterBy(filter);
        return variantTypeRepository.findAll(spec, pageable).map(variantTypeMapper::toResponse);
    }

//    @Cacheable(value = "variant_types", key = "#id")
    @Override
    public VariantType findById(Long id) {
        VariantType variantType = variantTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VariantType not found with id: " + id));

        if (!Constants.STATUS_ACTIVE.equals(variantType.getStatus())) {
            throw new ResourceNotFoundException("VariantType is inactive with id: " + id);
        }
        return variantType;
    }

    @Override
    @Transactional
    public VariantTypeResponse save(VariantTypeRequest request) {
        VariantType variantType = variantTypeMapper.toEntity(request);
        uniqueChecker.verify(variantTypeRepository, variantType, "name", variantType.getName());
        variantType.setStatus(Constants.STATUS_ACTIVE);
        VariantType savedVariantType = variantTypeRepository.save(variantType);
        return variantTypeMapper.toResponse(savedVariantType);
    }

//    @CacheEvict(value = "variant_types", key = "#id")
    @Override
    @Transactional
    public VariantTypeResponse update(Long id, VariantTypeRequest request) {
        VariantType variantType = findById(id);
        uniqueChecker.verify(variantTypeRepository, variantType, "name", request.name());
        variantTypeMapper.updateEntityFromRequest(request, variantType);
        VariantType updatedVariantType = variantTypeRepository.save(variantType);
        return variantTypeMapper.toResponse(updatedVariantType);
    }

//    @CacheEvict(value = "variant_types", key = "#id")
    @Override
    @Transactional
    public VariantTypeResponse delete(Long id) {
        VariantType variantType = findById(id);
        variantType.setStatus(Constants.STATUS_DELETE);
        VariantType deletedVariantType = variantTypeRepository.save(variantType);
        return variantTypeMapper.toResponse(deletedVariantType);
    }
}