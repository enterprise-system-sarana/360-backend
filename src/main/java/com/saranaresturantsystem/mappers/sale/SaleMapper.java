package com.saranaresturantsystem.mappers.sale;

import com.saranaresturantsystem.dto.request.sales.SaleRequest;
import com.saranaresturantsystem.dto.response.sales.SaleItemResponse;
import com.saranaresturantsystem.dto.response.sales.SaleResponse;
import com.saranaresturantsystem.entities.catalog.Product;
import com.saranaresturantsystem.entities.sales.SaleItems;
import com.saranaresturantsystem.entities.sales.Sales;
import com.saranaresturantsystem.repository.catalog.ProductRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class SaleMapper {
    @Mapping(target = "id", ignore      = true)
    @Mapping(target = "items", ignore   = true)
    @Mapping(target = "no", ignore      = true)
    @Mapping(target = "date", ignore    = true)
    @Mapping(target = "saleStatus", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "deleteFlag", ignore = true)
    public abstract Sales toEntity(SaleRequest request);

    @Mapping(source = "saleStatus", target = "status")
    public abstract SaleResponse toResponse(Sales sale);

    @Mapping(source = "quantity", target = "qty")
    @Mapping(source = "productSerialIds", target = "productSerialIds")
    @Mapping(source = "productId", target = "productName", qualifiedByName = "mapProductName")
    public abstract SaleItemResponse toResponse(SaleItems item);

    @Mapping(target = "id", ignore      = true)
    @Mapping(target = "items", ignore   = true)
    @Mapping(target = "no", ignore      = true)
    @Mapping(target = "date", ignore    = true)
    @Mapping(target = "saleStatus", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "deleteFlag", ignore = true)
    public abstract void updateFromRequest(SaleRequest request, @MappingTarget Sales sale);
    protected ProductRepository productRepository;

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Named("mapProductName")
    protected String mapProductName(Long productId) {
        if (productId == null || productRepository == null) {
            return null;
        }

        return productRepository.findById(productId)
                .map(Product::getCode)
                .orElse(null);
    }
}
