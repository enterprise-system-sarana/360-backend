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
public interface  SaleMapper {
    @Mapping(target = "id", ignore      = true)
    @Mapping(target = "items", ignore   = true)
    @Mapping(target = "no", ignore      = true)
    @Mapping(target = "date", ignore    = true)
    @Mapping(target = "saleStatus", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    Sales toEntity(SaleRequest request);

    @Mapping(source = "saleStatus", target = "status")
    SaleResponse toResponse(Sales sale);

    @Mapping(source = "quantity", target = "qty")
    @Mapping(target = "productSerialIds",  source = "productSerialIds")
    @Mapping(source = "product.models.name", target = "productName")
    @Mapping(source = "product.models.id", target = "productId")
    SaleItemResponse toResponse(SaleItems item);

    @Mapping(target = "id", ignore      = true)
    @Mapping(target = "items", ignore   = true)
    @Mapping(target = "no", ignore      = true)
    @Mapping(target = "date", ignore    = true)
    @Mapping(target = "saleStatus", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    void updateFromRequest(SaleRequest request, @MappingTarget Sales sale);
}
