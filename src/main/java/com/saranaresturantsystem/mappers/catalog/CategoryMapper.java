package com.saranaresturantsystem.mappers.catalog;

import com.saranaresturantsystem.dto.request.catalog.CategoryRequest;
import com.saranaresturantsystem.dto.response.catalog.CategoryResponse;
import com.saranaresturantsystem.entities.catalog.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);
    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryRequest request);
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(CategoryRequest request, @MappingTarget Category category);
}

