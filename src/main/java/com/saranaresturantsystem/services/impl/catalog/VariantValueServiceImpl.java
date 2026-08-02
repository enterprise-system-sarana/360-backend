package com.saranaresturantsystem.services.impl.catalog;

import com.saranaresturantsystem.common.UniqueChecker;
import com.saranaresturantsystem.dto.request.catalog.VariantValueRequest;
import com.saranaresturantsystem.dto.response.catalog.VariantValueResponse;
import com.saranaresturantsystem.entities.catalog.VariantType;
import com.saranaresturantsystem.entities.catalog.VariantValue;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.catalog.VariantValueMapper;
import com.saranaresturantsystem.repository.catalog.VariantValueRepository;
import com.saranaresturantsystem.services.interfaces.catalog.VariantTypeService;
import com.saranaresturantsystem.services.interfaces.catalog.VariantValueService;
import com.saranaresturantsystem.specification.catalog.variantvalue.VariantValueFilter;
import com.saranaresturantsystem.specification.catalog.variantvalue.VariantValueSpec;
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
public class VariantValueServiceImpl implements VariantValueService {
    private final VariantValueRepository variantValueRepository;
    private final VariantTypeService variantTypeService;
    private final UniqueChecker uniqueChecker;
    private final ObjectMapper objectMapper;
    private final VariantValueMapper variantValueMapper;

    @Transactional(readOnly = true)
    @Override
    public Page<VariantValueResponse> findAll(Map<String, String> params) {
        VariantValueFilter filter = objectMapper.convertValue(params, VariantValueFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<VariantValue> spec = VariantValueSpec.filterBy(filter);
        return variantValueRepository.findAll(spec, pageable).map(variantValueMapper::toResponse);
    }

    @Override
    public VariantValue findById(Long id) {
        VariantValue variantValue = variantValueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VariantValue not found with id: " + id));

        if (!"ACTIVE".equals(variantValue.getStatus())) {
            throw new ResourceNotFoundException("VariantValue is inactive with id: " + id);
        }
        return variantValue;
    }

    @Override
    @Transactional
    public VariantValueResponse save(VariantValueRequest request) {
        // Ensure parent VariantType exists and is active
        VariantType variantType = variantTypeService.findById(request.variantTypeId());

        VariantValue variantValue = variantValueMapper.toEntity(request);
        variantValue.setVariantTypeId(variantType.getId());

        uniqueChecker.verify(variantValueRepository, variantValue, "name", variantValue.getName());
        variantValue.setStatus("ACTIVE");

        VariantValue savedVariantValue = variantValueRepository.save(variantValue);

        // Ensure the relationship is populated for mapper to grab variantTypeName immediately
        savedVariantValue.setVariantType(variantType);

        return variantValueMapper.toResponse(savedVariantValue);
    }

    @Override
    @Transactional
    public VariantValueResponse update(Long id, VariantValueRequest request) {
        VariantValue variantValue = findById(id);

        if (request.variantTypeId() != null) {
            variantTypeService.findById(request.variantTypeId());
        }

        variantValueMapper.updateEntityFromRequest(request, variantValue);
        VariantValue updatedVariantValue = variantValueRepository.save(variantValue);
        return variantValueMapper.toResponse(updatedVariantValue);
    }

    @Override
    @Transactional
    public VariantValueResponse delete(Long id) {
        VariantValue variantValue = findById(id);
        variantValue.setStatus("INACTIVE");
        VariantValue deletedVariantValue = variantValueRepository.save(variantValue);
        return variantValueMapper.toResponse(deletedVariantValue);
    }
}