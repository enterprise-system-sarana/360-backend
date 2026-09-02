package com.saranaresturantsystem.services.interfaces.purchases;

import com.saranaresturantsystem.dto.request.purchases.ExpenseRequest;
import com.saranaresturantsystem.dto.response.purchases.ExpenseResponse;
import com.saranaresturantsystem.entities.purchase.Expenses;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ExpenseService {
    Page<ExpenseResponse> findAll(Map<String, String> params);
    Expenses findById(Long id);
    ExpenseResponse getById(Long id);
    ExpenseResponse save(ExpenseRequest request);
    ExpenseResponse update(Long id, ExpenseRequest request);
    void delete(Long id);
}