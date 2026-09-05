package com.saranaresturantsystem.mappers.catalog;


import com.saranaresturantsystem.dto.response.catalog.ProductSerialResponse;
import com.saranaresturantsystem.entities.catalog.ProductSerials;
import com.saranaresturantsystem.services.interfaces.catalog.ProductSerialService;
import com.saranaresturantsystem.services.interfaces.inventory.StoreService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring" , uses = {ProductSerialService.class , StoreService.class })
public interface ProductSerialMapper {


    @Mapping(target = "productId" , source = "product.id")
    @Mapping(target = "productName" , source = "product.name")
    @Mapping(target = "storeId" , source = "stores.id")
    @Mapping(target = "storeName" , source = "stores.name")
    ProductSerialResponse toResponse (ProductSerials productSerials );
}
