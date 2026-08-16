package com.saranaresturantsystem.mappers.purchase;

import com.saranaresturantsystem.dto.request.purchases.PurchaseItemRequest;
import com.saranaresturantsystem.dto.response.catalog.ProductSerialResponse;
import com.saranaresturantsystem.dto.response.purchases.PurchaseItemResponse;
import com.saranaresturantsystem.entities.catalog.ProductSerials;
import com.saranaresturantsystem.entities.purchase.PurchaseItem;
import com.saranaresturantsystem.services.interfaces.catalog.ProductService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring" ,uses = {ProductService.class })
public interface  PurchaseItemMapper {

    @Mapping(source = "purchase.id", target = "purchaseId")
    @Mapping(source = "product.models.name", target = "productName")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "productSerials", target = "serialNumbers")
    PurchaseItemResponse toResponse(PurchaseItem purchaseItem);

    ProductSerialResponse toSerialResponse(ProductSerials serial);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchase", ignore = true)
    @Mapping(target = "productSerials", ignore = true)
    @Mapping(target = "product",  source = "productId")
    PurchaseItem toEntity(PurchaseItemRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchase", ignore = true)
    @Mapping(target = "productSerials", ignore = true)
    @Mapping(target = "product",  source = "productId")
    void updateEntityFromRequest(PurchaseItemRequest request, @MappingTarget PurchaseItem purchaseItem);
}