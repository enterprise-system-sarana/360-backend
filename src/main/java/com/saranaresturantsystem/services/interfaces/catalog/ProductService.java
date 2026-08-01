package com.saranaresturantsystem.services.interfaces.catalog;

import com.saranaresturantsystem.dto.request.catalog.ProductRequest;
import com.saranaresturantsystem.dto.response.catalog.ProductResponse;
import com.saranaresturantsystem.entities.catalog.Product;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ProductService {
    Page<ProductResponse> findAll(Map<String , String> params);
    ProductResponse getById(Long id);
    ProductResponse save(ProductRequest request);
    ProductResponse update(Long id, ProductRequest request);
    void delete(Long id);
    Product findById(Long id);
}
