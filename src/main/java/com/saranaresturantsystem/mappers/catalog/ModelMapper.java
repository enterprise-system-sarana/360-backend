package com.saranaresturantsystem.mappers.catalog;

import com.saranaresturantsystem.dto.request.catalog.ModelRequest;
import com.saranaresturantsystem.dto.response.catalog.ModelResponse;
import com.saranaresturantsystem.entities.catalog.Model;
import com.saranaresturantsystem.services.interfaces.catalog.CategoryService;
import com.saranaresturantsystem.services.interfaces.catalog.BrandService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring" , uses = {CategoryService.class , BrandService.class})
public interface ModelMapper {

    @Mapping(target = "brandId", source = "brand.id")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "brandName", source = "brand.name")
    @Mapping(target = "categoryName", source = "category.name")
    ModelResponse toResponse (Model model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "categoryId")
    @Mapping(target = "brand", source = "brandId")
    Model toEntity(ModelRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "categoryId")
    @Mapping(target = "brand", source = "brandId")
    void updateEntityFromRequest(ModelRequest request, @MappingTarget Model model);
}
