package com.saranaresturantsystem.services.interfaces.sales;

import com.saranaresturantsystem.dto.request.sales.SaleRequest;
import com.saranaresturantsystem.dto.response.sales.SaleResponse;
import com.saranaresturantsystem.entities.sales.Sales;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface SaleService {
    SaleResponse create(SaleRequest request, String createdBy);

    SaleResponse getById(Long id);
    Page<SaleResponse> getAll(Map<String,String> params);
    SaleResponse update(Long id, SaleRequest request, String updatedBy);
    SaleResponse complete(Long id, String updatedBy);
    SaleResponse cancel(Long id, String updatedBy);
    SaleResponse returnSale(Long id, String updatedBy);
    void delete(Long id, String deletedBy);

    Sales findById(Long id);
}
