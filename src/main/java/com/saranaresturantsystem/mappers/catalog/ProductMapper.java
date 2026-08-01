package com.saranaresturantsystem.mappers.catalog;

import com.saranaresturantsystem.dto.request.catalog.ProductRequest;
import com.saranaresturantsystem.dto.response.catalog.ProductResponse;
import com.saranaresturantsystem.entities.catalog.Product;
import com.saranaresturantsystem.services.interfaces.catalog.CategoryService;
import com.saranaresturantsystem.services.interfaces.catalog.BrandService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring" , uses = {CategoryService.class , BrandService.class})
public interface ProductMapper {

    @Mapping(target = "brandId", source = "brand.id")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "brandName", source = "brand.name")
    @Mapping(target = "categoryName", source = "category.name")
    ProductResponse toResponse (Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "categoryId")
    @Mapping(target = "brand", source = "brandId")
    Product toEntity(ProductRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "categoryId")
    @Mapping(target = "brand", source = "brandId")
    void updateEntityFromRequest(ProductRequest request, @MappingTarget Product product);
}
