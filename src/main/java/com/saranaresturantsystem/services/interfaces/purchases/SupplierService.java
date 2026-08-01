package com.saranaresturantsystem.services.interfaces.purchases;

import com.saranaresturantsystem.dto.request.purchases.SupplierRequest;
import com.saranaresturantsystem.dto.response.purchases.SupplierResponse;
import com.saranaresturantsystem.entities.purchase.Suppliers;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface SupplierService {
    Page<SupplierResponse> findAll(Map<String , String>  params);
    Suppliers findById(Long id);
    SupplierResponse save (SupplierRequest request);
    SupplierResponse update(Long id, SupplierRequest request);
    SupplierResponse delete(Long id);

}
