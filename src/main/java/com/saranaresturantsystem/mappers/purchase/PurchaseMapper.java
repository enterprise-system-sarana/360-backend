package com.saranaresturantsystem.mappers.purchase;

import com.saranaresturantsystem.dto.request.purchases.PurchaseItemRequest;
import com.saranaresturantsystem.dto.request.purchases.PurchaseRequest;
import com.saranaresturantsystem.dto.response.catalog.ProductSerialResponse;
import com.saranaresturantsystem.dto.response.purchases.PurchaseItemResponse;
import com.saranaresturantsystem.dto.response.purchases.PurchaseResponse;
import com.saranaresturantsystem.entities.catalog.ProductSerials;
import com.saranaresturantsystem.entities.purchase.PurchaseItem;
import com.saranaresturantsystem.entities.purchase.Purchases;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {PurchaseItemMapper.class})
public interface PurchaseMapper {
    @Mapping(source = "purchaseItems", target = "items")
    PurchaseResponse toResponse(Purchases purchases);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchaseItems", ignore = true)
    Purchases toEntity(PurchaseRequest request);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchaseItems", ignore = true)
    void updateEntityFromRequest(PurchaseRequest request, @MappingTarget Purchases purchases);
}