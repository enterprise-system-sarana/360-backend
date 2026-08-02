package com.saranaresturantsystem.services.impl.catalog;

import com.saranaresturantsystem.dto.request.catalog.ProductRequest;
import com.saranaresturantsystem.dto.response.catalog.ProductResponse;
import com.saranaresturantsystem.entities.catalog.Product;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.catalog.ProductMapper;
import com.saranaresturantsystem.repository.catalog.ProductRepository;
import com.saranaresturantsystem.services.interfaces.catalog.ProductService;
import com.saranaresturantsystem.specification.catalog.product.ProductFilter;
import com.saranaresturantsystem.specification.catalog.product.ProductSpec;
import com.saranaresturantsystem.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductServiceimpl implements ProductService {
    private  final ProductRepository productRepository ;
    private  final ProductMapper productMapper ;
    private  final ObjectMapper objectMapper;

    @Override
    public Page<ProductResponse> findAll(Map<String, String> params) {
        ProductFilter filter = objectMapper.convertValue(params , ProductFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Product> spec = ProductSpec.filterBy(filter);
        return  productRepository.findAll(spec, pageable).map(productMapper::toResponse);
    }

    @Override
    public Product findById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Product", id));
        if (product.getStatus().equals("INACTIVE")){
            throw  new ResourceNotFoundException("Product" , id);
        }
        return product;
    }

    @Override
    public ProductResponse getById(Long id) {
        Product product = findById(id);
        return  productMapper.toResponse(product);
    }

    @Override
    public ProductResponse create(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        product.setStatus("ACTIVE");
        Product saveProduct = productRepository.save(product);
        return  productMapper.toResponse(product);
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        Product existingProduct = findById(id);
        productMapper.updateEntityFromRequest(request , existingProduct);
        Product saveProduct = productRepository.save(existingProduct);
        return productMapper.toResponse(saveProduct);
    }

    @Override
    public void delete(Long id) {
        Product product = findById(id);
        if(product.getStatus().equals("INACTIVE")){
            throw  new ResourceNotFoundException("Product" , id);
        }
    }
}
