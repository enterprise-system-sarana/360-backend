package com.saranaresturantsystem.mappers.catalog;

import com.saranaresturantsystem.dto.request.catalog.ProductVariantRequest;
import com.saranaresturantsystem.dto.response.catalog.ProductVariantResponse;
import com.saranaresturantsystem.entities.catalog.ProductVariant;
import com.saranaresturantsystem.services.interfaces.catalog.ProductService;
import com.saranaresturantsystem.services.interfaces.catalog.VariantValueService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring" , uses = {
        ProductService.class,
        VariantValueService.class,
        VariantValueMapper.class
})
public interface ProductVariantMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "categoryName", source = "product.models.category.name")
    @Mapping(target = "brandName", source = "product.models.brand.name")
    @Mapping(target = "modelName", source = "product.models.name")
    ProductVariantResponse toResponse (ProductVariant variant);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", source = "productId")
    @Mapping(target = "variantValues", source = "variantValueIds")
    ProductVariant toEntity(ProductVariantRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", source = "productId")
    @Mapping(target = "variantValues", source = "variantValueIds")
    void updateFromRequest(ProductVariantRequest request , @MappingTarget ProductVariant variant);
}
