package com.saranaresturantsystem.services.interfaces.inventory;

import com.saranaresturantsystem.dto.request.inventory.StoreRequest;
import com.saranaresturantsystem.dto.response.inventory.StoreResponse;
import com.saranaresturantsystem.entities.inventory.Stores;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface StoreService {
    Page<StoreResponse> findAll(Map<String , String> params);
    StoreResponse save(StoreRequest request);
    StoreResponse update(Long id, StoreRequest request);
    StoreResponse getById(Long id);
    void delete(Long id);
    Stores findById(@Positive Long id);
}
