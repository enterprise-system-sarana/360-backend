package com.saranaresturantsystem.mappers.inventory;


import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.dto.response.inventory.StockResponse;
import com.saranaresturantsystem.entities.inventory.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring" )
public interface StockMapper {
    @Mapping(source = "product.models.name", target = "productName")
    @Mapping(source = "product.models.id", target = "productId")
    @Mapping(source = "stores.id", target = "storeId")
    @Mapping(source = "stores.name", target = "storeName")
    @Mapping(source = "product.reorderLevel", target = "reorderLevel")
    @Mapping(target = "status", expression = "java(getStatus(stock))")
    StockResponse toResponse(Stock stock);

    default String getStatus(Stock stock) {
        if (stock == null) {
            return null;
        }
        if (stock.getQuantity() == null || stock.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            return Constants.OUT_STOCK;
        }

        BigDecimal alertQty = stock.getAlertQuantity();
        if ((alertQty == null || alertQty.compareTo(BigDecimal.ZERO) <= 0)
                && stock.getProduct() != null && stock.getProduct().getReorderLevel() != null) {
            alertQty = BigDecimal.valueOf(stock.getProduct().getReorderLevel());
        }

        if (alertQty != null
                && alertQty.compareTo(BigDecimal.ZERO) > 0
                && stock.getQuantity().compareTo(alertQty) <= 0) {
            return Constants.LOW_STOCK;
        }

        return Constants.IN_STOCK;
    }


}
