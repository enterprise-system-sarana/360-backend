package com.saranaresturantsystem.mappers;

import com.saranaresturantsystem.dto.request.CategoryRequest;
import com.saranaresturantsystem.dto.response.CategoryResponse;
import com.saranaresturantsystem.entities.Category;
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

