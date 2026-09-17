package com.saranaresturantsystem.mappers.sale;

import com.saranaresturantsystem.dto.request.sales.SaleRequest;
import com.saranaresturantsystem.dto.response.catalog.ProductSerialResponse;
import com.saranaresturantsystem.dto.response.sales.SaleItemResponse;
import com.saranaresturantsystem.dto.response.sales.SaleResponse;
import com.saranaresturantsystem.entities.catalog.ProductSerials;
import com.saranaresturantsystem.entities.sales.SaleItems;
import com.saranaresturantsystem.entities.sales.Sales;
import com.saranaresturantsystem.mappers.catalog.ProductSerialMapper;
import com.saranaresturantsystem.repository.catalog.ProductSerialsRepository;
import com.saranaresturantsystem.services.interfaces.customer.CustomerService;
import com.saranaresturantsystem.services.interfaces.finances.BankService;
import com.saranaresturantsystem.services.interfaces.inventory.StoreService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", uses = {BankService.class, CustomerService.class, StoreService.class, ProductSerialMapper.class})
public abstract class SaleMapper {

    @Autowired
    protected ProductSerialsRepository productSerialsRepository;

    @Autowired
    protected ProductSerialMapper productSerialMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "no", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "saleStatus", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    // Ignore audit / audit-related fields if they are managed automatically or not in request
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "grandTotal", ignore = true)
    @Mapping(target = "returnAmount", ignore = true)
    public abstract Sales toEntity(SaleRequest request);

    @Mapping(source = "saleStatus", target = "status")
    @Mapping(source = "store.id", target = "storeId")
    @Mapping(source = "store.name", target = "storeName")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "banks.id", target = "bankId")
    @Mapping(source = "banks.name", target = "bankName")
    @Mapping(source = "items", target = "items")
    public abstract SaleResponse toResponse(Sales sale);

    @Mapping(source = "quantity", target = "qty")
    @Mapping(target = "productSerialIds", source = "productSerialIds")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(target = "serialNumbers", expression = "java(mapSerialNumbers(item.getProductSerialIds()))")
    @Mapping(target = "serials", expression = "java(mapProductSerials(item.getProductSerialIds()))")
    public abstract SaleItemResponse toResponse(SaleItems item);

    protected List<String> mapSerialNumbers(List<Long> productSerialIds) {
        if (productSerialIds == null || productSerialIds.isEmpty() || productSerialsRepository == null) {
            return Collections.emptyList();
        }
        return productSerialsRepository.findAllById(productSerialIds).stream()
                .map(ProductSerials::getBarcode)
                .filter(b -> b != null && !b.isBlank())
                .toList();
    }

    protected List<ProductSerialResponse> mapProductSerials(List<Long> productSerialIds) {
        if (productSerialIds == null || productSerialIds.isEmpty() || productSerialsRepository == null) {
            return Collections.emptyList();
        }
        return productSerialsRepository.findAllById(productSerialIds).stream()
                .map(productSerialMapper::toResponse)
                .toList();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "no", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "saleStatus", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "grandTotal", ignore = true)
    @Mapping(target = "returnAmount", ignore = true)
    public abstract void updateFromRequest(SaleRequest request, @MappingTarget Sales sale);
}