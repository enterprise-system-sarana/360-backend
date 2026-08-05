package com.saranaresturantsystem.services.impl.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.common.UniqueChecker;
import com.saranaresturantsystem.dto.request.inventory.StoreRequest;
import com.saranaresturantsystem.dto.response.inventory.StoreResponse;
import com.saranaresturantsystem.entities.inventory.Stores;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.inventory.StoreMapper;
import com.saranaresturantsystem.repository.Inventory.StoreRepsoitory;
import com.saranaresturantsystem.services.interfaces.inventory.StoreService;
import com.saranaresturantsystem.specification.inventory.Stores.StoreFilter;
import com.saranaresturantsystem.specification.inventory.Stores.StoreSpec;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreServiceImpl implements StoreService {
    private final StoreRepsoitory storeRepsoitory;
    private final ObjectMapper objectMapper;
    private final StoreMapper storeMapper;
    private final UniqueChecker uniqueChecker;

    @Cacheable(value = "stores", key = "'all'")
    @Override
    public Page<StoreResponse> findAll(Map<String, String> params) {
        StoreFilter filter = objectMapper.convertValue(params, StoreFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Stores> spec = StoreSpec.filterBy(filter);
        return storeRepsoitory.findAll(spec, pageable).map(storeMapper::toResponse);
    }

    @Override
    public StoreResponse save(StoreRequest request) {
        Stores stores = storeMapper.toEntity(request);
        uniqueChecker.verify(storeRepsoitory, stores, "name", stores.getName());
        uniqueChecker.verify(storeRepsoitory, stores, "code", stores.getCode());
        stores.setStatus("ACTIVE");
        Stores savedStore = storeRepsoitory.save(stores);
        return storeMapper.toResponse(savedStore);
    }

    @CacheEvict(value = "stores", key = "#id")
    @Override
    public StoreResponse update(Long id, StoreRequest request) {
        Stores stores = findById(id);
        storeMapper.updateEnityFromRequest(request, stores);
        Stores save = storeRepsoitory.save(stores);
        return storeMapper.toResponse(save);
    }

    @Cacheable(value = "stores", key = "#id")
    @Override
    public StoreResponse getById(Long id) {
        Stores stores = findById(id);
        return storeMapper.toResponse(stores);
    }

    @CacheEvict(value = "stores", key = "#id")
    @Override
    public void delete(Long id) {
        Stores stores = findById(id);
        stores.setStatus("INACTIVE");
        storeRepsoitory.save(stores);
    }

    @Cacheable(value = "stores", key = "#id")
    @Override
    public Stores findById(Long id) {
        Stores stores = storeRepsoitory.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stores : " + id));
        if (stores.getStatus().equals("INACTIVE")) {
            throw new ResourceNotFoundException("Stores : " + id);
        }
        return stores;
    }
}
