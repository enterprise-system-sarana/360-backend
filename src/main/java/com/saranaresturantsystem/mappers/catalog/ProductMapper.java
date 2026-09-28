package com.saranaresturantsystem.mappers.catalog;

import com.saranaresturantsystem.dto.request.catalog.ProductRequest;
import com.saranaresturantsystem.dto.response.catalog.ProductResponse;
import com.saranaresturantsystem.entities.catalog.Product;
import com.saranaresturantsystem.services.interfaces.catalog.ModelService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;

@Mapper(componentModel = "spring" , uses = {ModelService.class, VariantValueMapper.class})
public interface ProductMapper {

    @Mapping(target = "modelId" ,source = "models.id")
    @Mapping(target = "modelName" ,source = "models.name")
    @Mapping(target = "brandName" ,source = "models.brand.name")
    @Mapping(target = "categoryName" ,source = "models.category.name")
    @Mapping(target = "variantValues" ,source = "variantValues")
    @Mapping(target = "qty", expression = "java(calculateTotalQty(product))")
    @Mapping(target = "totalPrice", expression = "java(calculateTotalPrice(product))")
    @Mapping(target = "totalSale", expression = "java(calculateTotalSale(product))")
    @Mapping(target = "profit", expression = "java(calculateProfit(product))")
    ProductResponse toResponse (Product product);

    default BigDecimal calculateTotalQty(Product product) {
        if (product == null || product.getStocks() == null || product.getStocks().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return product.getStocks().stream()
                    .map(stock -> stock.getQuantity() != null ? stock.getQuantity() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    default BigDecimal calculateTotalPrice(Product product) {
        return product == null || product.getCostPrice() == null
                ? BigDecimal.ZERO
                : product.getCostPrice().multiply(calculateTotalQty(product));
    }

    default BigDecimal calculateTotalSale(Product product) {
        return product == null || product.getSalePrice() == null
                ? BigDecimal.ZERO
                : product.getSalePrice().multiply(calculateTotalQty(product));
    }

    default BigDecimal calculateProfit(Product product) {
        return calculateTotalSale(product).subtract(calculateTotalPrice(product));
    }

    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "models" , source = "modelId")
    @Mapping(target = "variantValues" , ignore = true)
    Product toEntity(ProductRequest request);

    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "models" , source = "modelId")
    @Mapping(target = "variantValues" , ignore = true)
    void updateEntityFromRequest(ProductRequest request , @MappingTarget Product product);
}
