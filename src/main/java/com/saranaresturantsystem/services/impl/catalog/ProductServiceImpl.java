package com.saranaresturantsystem.services.impl.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.dto.request.catalog.ProductRequest;
import com.saranaresturantsystem.dto.response.catalog.ProductResponse;
import com.saranaresturantsystem.entities.catalog.Product;
import com.saranaresturantsystem.entities.catalog.VariantValue;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.catalog.ProductMapper;
import com.saranaresturantsystem.repository.catalog.ProductRepository;
import com.saranaresturantsystem.repository.catalog.VariantValueRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final VariantValueRepository variantValueRepository;
    private final ProductMapper productMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(Map<String, String> params) {
        ProductFilter filter = objectMapper.convertValue(params, ProductFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Product> spec = ProductSpec.filterBy(filter);
        return productRepository.findAll(spec, pageable).map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Product findById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id));
        if (product.getStatus().equals(Constants.STATUS_INIT) || product.getStatus().equals(Constants.STATUS_DELETE)) {
            throw new ResourceNotFoundException("Product", id);
        }
        return product;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Product product = findById(id);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        product.setStatus(Constants.STATUS_ACTIVE);
        if (request.variantValueIds() != null && !request.variantValueIds().isEmpty()) {
            List<VariantValue> variantValues = variantValueRepository.findAllById(request.variantValueIds());
            product.setVariantValues(variantValues);
        }
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product existingProduct = findById(id);
        productMapper.updateEntityFromRequest(request, existingProduct);
        if (request.variantValueIds() != null) {
            List<VariantValue> variantValues = variantValueRepository.findAllById(request.variantValueIds());
            existingProduct.setVariantValues(variantValues);
        }
        Product saveProduct = productRepository.save(existingProduct);
        return productMapper.toResponse(saveProduct);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = findById(id);
        product.setStatus(Constants.STATUS_DELETE);
        productRepository.save(product);
    }
}
