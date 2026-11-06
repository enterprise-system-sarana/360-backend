package com.saranaresturantsystem.mappers.inventory;


import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.dto.response.inventory.StockResponse;
import com.saranaresturantsystem.entities.inventory.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface StockMapper {
    @Mapping(target = "productId", expression = "java(getProductId(stock))")
    @Mapping(target = "productName", expression = "java(getProductName(stock))")
    @Mapping(source = "product.models.id", target = "modelId")
    @Mapping(source = "product.models.name", target = "modelName")
    @Mapping(source = "product.models.brand.id", target = "brandId")
    @Mapping(source = "product.models.brand.name", target = "brandName")
    @Mapping(source = "product.models.category.id", target = "categoryId")
    @Mapping(source = "product.models.category.name", target = "categoryName")
    @Mapping(source = "stores.id", target = "storeId")
    @Mapping(source = "stores.name", target = "storeName")
    @Mapping(source = "product.reorderLevel", target = "reorderLevel")
    @Mapping(target = "status", expression = "java(getStatus(stock))")
    StockResponse toResponse(Stock stock);

    default Long getProductId(Stock stock) {
        if (stock == null || stock.getProduct() == null) {
            return null;
        }
        return stock.getProduct().getId();
    }

    default String getProductName(Stock stock) {
        if (stock == null || stock.getProduct() == null) {
            return null;
        }
        if (stock.getProduct().getModels() != null && stock.getProduct().getModels().getName() != null && !stock.getProduct().getModels().getName().isBlank()) {
            return stock.getProduct().getModels().getName();
        }
        return stock.getProduct().getName();
    }

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
