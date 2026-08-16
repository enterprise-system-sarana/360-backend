package com.saranaresturantsystem.mappers.inventory;


import com.saranaresturantsystem.dto.response.inventory.StockResponse;
import com.saranaresturantsystem.entities.inventory.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring" )
public interface StockMapper {
    @Mapping(source = "product.models.name" , target = "productName")
    @Mapping(source = "product.models.id" , target = "productId")
    @Mapping(source = "stores.id",target = "storeId")
    @Mapping(source = "stores.name",target = "storeName")
    @Mapping(source = "product.reorderLevel", target = "reorderLevel")
    StockResponse toResponse(Stock stock);
}
