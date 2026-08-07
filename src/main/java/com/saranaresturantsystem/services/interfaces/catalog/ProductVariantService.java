//package com.saranaresturantsystem.services.interfaces.catalog;
//
//import com.saranaresturantsystem.dto.request.catalog.ProductVariantRequest;
//import com.saranaresturantsystem.dto.response.catalog.ProductVariantResponse;
//import com.saranaresturantsystem.entities.catalog.ProductVariant;
//import org.springframework.data.domain.Page;
//
//import java.util.Map;
//
//public interface ProductVariantService {
//    Page<ProductVariantResponse> findAll(Map<String, String> params);
//
//    ProductVariant findById(Long id);
//
//    ProductVariantResponse getById(Long id);
//
//    ProductVariantResponse create(ProductVariantRequest request);
//
//    ProductVariantResponse update(Long id, ProductVariantRequest request);
//
//    void delete(Long id);
//}
