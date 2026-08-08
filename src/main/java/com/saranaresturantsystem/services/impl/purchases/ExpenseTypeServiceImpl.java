package com.saranaresturantsystem.services.impl.purchases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.dto.request.purchases.ExpenseTypeRequest;
import com.saranaresturantsystem.dto.response.purchases.ExpenseTypeResponse;
import com.saranaresturantsystem.entities.purchase.ExpenseType;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.purchase.ExpenseTypeMapper;
import com.saranaresturantsystem.repository.purchases.ExpenseTypeRepository;
import com.saranaresturantsystem.services.interfaces.purchases.ExpenseTypeService;
import com.saranaresturantsystem.specification.purchases.ExpenseTypes.ExpenseTypeFilter;
import com.saranaresturantsystem.specification.purchases.ExpenseTypes.ExpenseTypeSpec;
import com.saranaresturantsystem.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseTypeServiceImpl implements ExpenseTypeService {

    private final ExpenseTypeRepository expenseTypeRepository;
    private final ExpenseTypeMapper expenseTypeMapper;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    @Override
    public Page<ExpenseTypeResponse> findAll(Map<String, String> params) {
        ExpenseTypeFilter filter = objectMapper.convertValue(params, ExpenseTypeFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<ExpenseType> spec = ExpenseTypeSpec.filterBy(filter);
        return expenseTypeRepository.findAll(spec, pageable).map(expenseTypeMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseType findById(Long id) {
        ExpenseType expenseType = expenseTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense Type not found with id: " + id));
        if ("INACTIVE".equalsIgnoreCase(expenseType.getStatus())) {
            throw new ResourceNotFoundException("Expense Type not found with id: " + id);
        }
        return expenseType;
    }

    @Override
    @Transactional
    public ExpenseTypeResponse save(ExpenseTypeRequest request) {
        ExpenseType entity = expenseTypeMapper.toEntity(request);
        entity.setStatus("ACTIVE");
        ExpenseType saved = expenseTypeRepository.save(entity);
        return expenseTypeMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ExpenseTypeResponse update(Long id, ExpenseTypeRequest request) {
        ExpenseType entity = findById(id);
        expenseTypeMapper.updateEntityFromRequest(request, entity);
        ExpenseType updated = expenseTypeRepository.save(entity);
        return expenseTypeMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ExpenseType entity = findById(id);
        entity.setStatus("INACTIVE");
        expenseTypeRepository.save(entity);
    }
}