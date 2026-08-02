package com.saranaresturantsystem.services.interfaces.catalog;

import com.saranaresturantsystem.dto.request.catalog.ModelRequest;
import com.saranaresturantsystem.dto.response.catalog.ModelResponse;
import com.saranaresturantsystem.entities.catalog.Model;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ModelService {
    Page<ModelResponse> findAll(Map<String , String> params);
    ModelResponse getById(Long id);
    ModelResponse save(ModelRequest request);
    ModelResponse update(Long id, ModelRequest request);
    void delete(Long id);
    Model findById(Long id);
}
