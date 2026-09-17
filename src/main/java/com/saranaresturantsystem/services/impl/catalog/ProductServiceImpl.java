package com.saranaresturantsystem.services.impl.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.dto.request.catalog.ProductRequest;
import com.saranaresturantsystem.dto.response.catalog.ProductDetailResponse;
import com.saranaresturantsystem.dto.response.catalog.ProductResponse;
import com.saranaresturantsystem.entities.catalog.Product;
import com.saranaresturantsystem.entities.catalog.ProductSerials;
import com.saranaresturantsystem.entities.catalog.VariantValue;
import com.saranaresturantsystem.entities.inventory.Stock;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.catalog.ProductMapper;
import com.saranaresturantsystem.repository.Inventory.StockRepository;
import com.saranaresturantsystem.repository.catalog.ProductRepository;
import com.saranaresturantsystem.repository.catalog.ProductSerialsRepository;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final VariantValueRepository variantValueRepository;
    private final ProductSerialsRepository productSerialsRepository;
    private final StockRepository stockRepository;
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
    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(Long id) {
        Product product = findById(id);

        Long brandId = null;
        String brandName = null;
        Long categoryId = null;
        String categoryName = null;
        Long modelId = null;
        String modelName = null;

        if (product.getModels() != null) {
            modelId = product.getModels().getId();
            modelName = product.getModels().getName();
            if (product.getModels().getBrand() != null) {
                brandId = product.getModels().getBrand().getId();
                brandName = product.getModels().getBrand().getName();
            }
            if (product.getModels().getCategory() != null) {
                categoryId = product.getModels().getCategory().getId();
                categoryName = product.getModels().getCategory().getName();
            }
        }

        List<ProductSerials> serialEntities = productSerialsRepository.findByProductIdOrderByIdAsc(id);

        List<ProductDetailResponse.SerialInfo> serials = serialEntities.stream()
                .map(serial -> ProductDetailResponse.SerialInfo.builder()
                        .id(serial.getId())
                        .serialNumber(serial.getBarcode())
                        .barcode(product.getCode() != null ? product.getCode() : serial.getBarcode())
                        .costPrice(serial.getCost() != null ? serial.getCost() : product.getCostPrice())
                        .sellingPrice(serial.getPrice() != null ? serial.getPrice() : product.getSalePrice())
                        .status(serial.getStatus())
                        .build())
                .toList();

        List<Stock> stockEntities = stockRepository.findByProductId(id);
        BigDecimal totalStockQty = BigDecimal.ZERO;
        if (stockEntities != null && !stockEntities.isEmpty()) {
            for (Stock s : stockEntities) {
                if (s.getQuantity() != null) {
                    totalStockQty = totalStockQty.add(s.getQuantity());
                }
            }
        } else if (!serialEntities.isEmpty()) {
            totalStockQty = BigDecimal.valueOf(serialEntities.size());
        }

        long availableSerialsCount = serialEntities.stream()
                .filter(s -> s.getStatus() != null && (
                        Constants.AVAILABLE.equalsIgnoreCase(s.getStatus()) ||
                        "IN_STOCK".equalsIgnoreCase(s.getStatus()) ||
                        Constants.STATUS_ACTIVE.equalsIgnoreCase(s.getStatus())
                ))
                .count();

        return ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .brandId(brandId)
                .brandName(brandName)
                .categoryId(categoryId)
                .categoryName(categoryName)
                .modelId(modelId)
                .modelName(modelName)
                .reorderLevel(product.getReorderLevel())
                .quantity(totalStockQty)
                .availableSerials(availableSerialsCount)
                .serials(serials)
                .build();
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
