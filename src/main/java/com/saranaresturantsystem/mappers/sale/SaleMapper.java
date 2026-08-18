package com.saranaresturantsystem.mappers.sale;

import com.saranaresturantsystem.dto.request.sales.SaleRequest;
import com.saranaresturantsystem.dto.response.sales.SaleItemResponse;
import com.saranaresturantsystem.dto.response.sales.SaleResponse;
import com.saranaresturantsystem.entities.sales.SaleItems;
import com.saranaresturantsystem.entities.sales.Sales;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SaleMapper {

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
    Sales toEntity(SaleRequest request);

    @Mapping(source = "saleStatus", target = "status")
    SaleResponse toResponse(Sales sale);

    @Mapping(source = "quantity", target = "qty")
    @Mapping(target = "productSerialIds", source = "productSerialIds")
    // ប្តូរពី product.name ទៅជា product.code (ផ្អែកតាម Error ដែល MapStruct បានណែនាំ)
    // ឬប្រសិនបើ Product របស់អ្នកមាន name តាមរយៈ relation ផ្សេង សូមបញ្ជាក់វា (ឧ. product.model.name)
    @Mapping(source = "product.code", target = "productName")
    @Mapping(source = "product.id", target = "productId")
    SaleItemResponse toResponse(SaleItems item);

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
    void updateFromRequest(SaleRequest request, @MappingTarget Sales sale);
}