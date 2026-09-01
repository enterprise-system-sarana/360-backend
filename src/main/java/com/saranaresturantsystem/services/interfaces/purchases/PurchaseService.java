package com.saranaresturantsystem.services.interfaces.purchases;

import com.saranaresturantsystem.dto.request.purchases.PurchaseRequest;
import com.saranaresturantsystem.dto.response.purchases.PurchaseResponse;
import com.saranaresturantsystem.entities.purchase.Purchase;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface PurchaseService {
    Page<PurchaseResponse> findAll(Map<String, String> params);
    Purchase findById(Long id); // សម្រាប់ប្រើប្រាស់ផ្ទៃក្នុង (Internal Use)
    PurchaseResponse findByIdResponse(Long id); // សម្រាប់ទាញយកទិន្នន័យចេញជា DTO
    PurchaseResponse save(PurchaseRequest request);
    PurchaseResponse update(Long id, PurchaseRequest request);
    PurchaseResponse delete(Long id);
}