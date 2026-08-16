package com.saranaresturantsystem.services.impl.purchases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.common.UniqueChecker;
import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.dto.request.purchases.SupplierRequest;
import com.saranaresturantsystem.dto.response.purchases.SupplierResponse;
import com.saranaresturantsystem.entities.purchase.Suppliers;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.purchase.SupplierMapper;
import com.saranaresturantsystem.repository.purchases.SupplierRepository;
import com.saranaresturantsystem.services.interfaces.purchases.SupplierService;
import com.saranaresturantsystem.specification.purchases.supplier.SupplierFilter;
import com.saranaresturantsystem.specification.purchases.supplier.SupplierSpec;
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
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;
    private final UniqueChecker uniqueChecker;
    private final ObjectMapper objectMapper;
    private final SupplierMapper supplierMappers;

    @Transactional
    @Override
    public Page<SupplierResponse> findAll(Map<String, String> params) {
        SupplierFilter filter = objectMapper.convertValue(params, SupplierFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Suppliers> spec = SupplierSpec.filterBy(filter);
        return supplierRepository.findAll(spec, pageable).map(supplierMappers::toResponse);
    }

//    @Cacheable(value = "suppliers", key = "#id")
    @Override
    public Suppliers findById(Long id) {
        Suppliers suppliers = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier : " + id));
        if (suppliers.getStatus().equals(Constants.STATUS_INIT) || suppliers.getStatus().equals(Constants.STATUS_DELETE)) {
            throw new ResourceNotFoundException("Supplier : " + id);
        }
        return suppliers;
    }

    @Override
    @Transactional
    public SupplierResponse save(SupplierRequest request) {
        Suppliers suppliers = supplierMappers.toEntity(request);
        uniqueChecker.verify(supplierRepository, suppliers, "name", suppliers.getName());
        uniqueChecker.verify(supplierRepository, suppliers, "code", suppliers.getCode());
        suppliers.setStatus(Constants.STATUS_ACTIVE);
        Suppliers savedSupplier = supplierRepository.save(suppliers);
        return supplierMappers.toResponse(savedSupplier);
    }

//    @CacheEvict(value = "suppliers", key = "#id")
    @Override
    public SupplierResponse update(Long id, SupplierRequest request) {
        Suppliers suppliers = findById(id);
        uniqueChecker.verify(supplierRepository, suppliers, "name", request.name());
        uniqueChecker.verify(supplierRepository, suppliers, "code", request.code());
        supplierMappers.updateEntityFromRequest(request, suppliers);
        Suppliers save = supplierRepository.save(suppliers);
        return supplierMappers.toResponse(save);

    }

//    @CacheEvict(value = "suppliers", key = "#id")
    @Override
    public SupplierResponse delete(Long id) {
        Suppliers suppliers = findById(id);
        suppliers.setStatus(Constants.STATUS_DELETE);
        Suppliers save = supplierRepository.save(suppliers);
        return supplierMappers.toResponse(save);
    }
}
