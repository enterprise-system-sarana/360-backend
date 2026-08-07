package com.saranaresturantsystem.mappers.purchase;

import com.saranaresturantsystem.dto.request.purchases.PurchaseItemRequest;
import com.saranaresturantsystem.dto.response.purchases.PurchaseItemResponse;
import com.saranaresturantsystem.entities.purchase.Purchase_Items;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PurchaseItemMapper {

    @Mapping(source = "purchase.id", target = "purchaseId")
    PurchaseItemResponse toResponse(Purchase_Items purchaseItem);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchase", ignore = true)
    Purchase_Items toEntity(PurchaseItemRequest request);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchase", ignore = true)
    void updateEntityFromRequest(PurchaseItemRequest request, @MappingTarget Purchase_Items purchaseItem);
}