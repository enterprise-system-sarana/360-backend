package com.saranaresturantsystem.services.impl.catalog;

import com.saranaresturantsystem.common.UniqueChecker;
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
public class ProductServiceImpl implements ProductService {
    private  final ProductRepository productRepository ;
    private  final UniqueChecker uniqueChecker ;
    private  final ObjectMapper objectMapper ;
    private  final ProductMapper productMapper ;
    @Override
    public Page<ProductResponse> findAll(Map<String, String> params) {
        ProductFilter filter = objectMapper.convertValue(params, ProductFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Product> spec = ProductSpec.filterBy(filter);
        return productRepository.findAll(spec, pageable).map(productMapper::toResponse);
    }

    @Override
    public ProductResponse getById(Long id) {
        Product product = findById(id);
        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse save(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        uniqueChecker.verify(productRepository , product, "Product",  product.getName());
        product.setStatus("ACTIVE");
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        Product existingProduct = findById(id);
        productMapper.updateEntityFromRequest(request, existingProduct);
        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public void delete(Long id) {
        Product product = findById(id);
        product.setStatus("INACTIVE");
        productRepository.save(product);
    }

    @Override
    public Product findById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Product", id));
        if (product.getStatus().equals("INACTIVE")){
            throw new ResourceNotFoundException("Product", id);
        }
        return product;
    }
}
