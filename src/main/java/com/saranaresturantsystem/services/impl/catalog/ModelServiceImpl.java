package com.saranaresturantsystem.services.impl.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.common.UniqueChecker;
import com.saranaresturantsystem.dto.request.catalog.ModelRequest;
import com.saranaresturantsystem.dto.response.catalog.ModelResponse;
import com.saranaresturantsystem.entities.catalog.Model;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.catalog.ModelMapper;
import com.saranaresturantsystem.repository.catalog.ModelRepository;
import com.saranaresturantsystem.services.interfaces.catalog.ModelService;
import com.saranaresturantsystem.specification.catalog.model.ModelFilter;
import com.saranaresturantsystem.specification.catalog.model.ModelSpec;
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
public class ModelServiceImpl implements ModelService {
    private final ModelRepository modelRepository;
    private final UniqueChecker uniqueChecker;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

    @Cacheable(value = "models", key = "'all'")
    @Override
    public Page<ModelResponse> findAll(Map<String, String> params) {
        ModelFilter filter = objectMapper.convertValue(params, ModelFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Model> spec = ModelSpec.filterBy(filter);
        return modelRepository.findAll(spec, pageable).map(modelMapper::toResponse);
    }

    @Cacheable(value = "models", key = "#id")
    @Override
    public ModelResponse getById(Long id) {
        Model model = findById(id);
        return modelMapper.toResponse(model);
    }

    @Override
    public ModelResponse save(ModelRequest request) {
        Model model = modelMapper.toEntity(request);
        uniqueChecker.verify(modelRepository, model, "Model", model.getName());
        model.setStatus("ACTIVE");
        Model savedModel = modelRepository.save(model);
        return modelMapper.toResponse(savedModel);
    }

    @CacheEvict(value = "models", key = "#id")
    @Override
    public ModelResponse update(Long id, ModelRequest request) {
        Model existingModel = findById(id);
        modelMapper.updateEntityFromRequest(request, existingModel);
        Model updatedModel = modelRepository.save(existingModel);
        return modelMapper.toResponse(updatedModel);
    }

    @CacheEvict(value = "models", key = "#id")
    @Override
    public void delete(Long id) {
        Model model = findById(id);
        model.setStatus("INACTIVE");
        modelRepository.save(model);
    }

    @Cacheable(value = "models", key = "#id")
    @Override
    public Model findById(Long id) {
        Model model = modelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Model", id));
        if (model.getStatus().equals("INACTIVE")) {
            throw new ResourceNotFoundException("Model", id);
        }
        return model;
    }
}
