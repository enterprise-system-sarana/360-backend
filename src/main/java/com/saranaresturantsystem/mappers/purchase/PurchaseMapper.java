package com.saranaresturantsystem.mappers.purchase;

import com.saranaresturantsystem.dto.request.purchases.PurchaseRequest;
import com.saranaresturantsystem.dto.response.purchases.PurchaseResponse;
import com.saranaresturantsystem.entities.purchase.Purchase;
import com.saranaresturantsystem.services.interfaces.finances.BankService;
import com.saranaresturantsystem.services.interfaces.inventory.StoreService;
import com.saranaresturantsystem.services.interfaces.purchases.SupplierService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {PurchaseItemMapper.class , StoreService.class , SupplierService.class ,
BankService.class
})
public interface PurchaseMapper {
    @Mapping(source = "purchaseItems", target = "items")
    @Mapping(source = "suppliers.id" , target = "supplierId")
    @Mapping(source = "suppliers.name" , target = "supplierName")
    @Mapping(source = "stores.id" , target = "storeId")
    @Mapping(source = "stores.name" , target = "storeName")
    @Mapping(source = "banks.id" , target = "bankId")
    @Mapping(source = "banks.name" , target = "bankName")
    PurchaseResponse toResponse(Purchase purchases);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchaseItems", ignore = true)
    Purchase toEntity(PurchaseRequest request);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchaseItems", ignore = true)
    void updateEntityFromRequest(PurchaseRequest request, @MappingTarget Purchase purchases);
}