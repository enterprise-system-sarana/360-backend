package com.saranaresturantsystem.services.interfaces.purchases;

import com.saranaresturantsystem.dto.request.purchases.ExpenseTypeRequest;
import com.saranaresturantsystem.dto.response.purchases.ExpenseTypeResponse;
import com.saranaresturantsystem.entities.purchase.ExpenseType;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ExpenseTypeService {
    Page<ExpenseTypeResponse> findAll(Map<String, String> params);
    ExpenseTypeResponse getById(Long id);
    ExpenseType findById(Long id);
    ExpenseTypeResponse save(ExpenseTypeRequest request);
    ExpenseTypeResponse update(Long id, ExpenseTypeRequest request);
    void delete(Long id);
}