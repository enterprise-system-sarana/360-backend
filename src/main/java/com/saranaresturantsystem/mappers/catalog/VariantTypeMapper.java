package com.saranaresturantsystem.mappers.catalog;

import com.saranaresturantsystem.dto.request.catalog.VariantTypeRequest;
import com.saranaresturantsystem.dto.response.catalog.VariantTypeResponse;
import com.saranaresturantsystem.dto.response.catalog.VariantValueItemResponse;
import com.saranaresturantsystem.entities.catalog.VariantType;
import com.saranaresturantsystem.entities.catalog.VariantValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VariantTypeMapper {

    @Mapping(target = "values", source = "variantValues")
    VariantTypeResponse toResponse(VariantType variantType);

    VariantValueItemResponse toValueItemResponse(VariantValue variantValue);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "variantValues", ignore = true)
    VariantType toEntity(VariantTypeRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "variantValues", ignore = true)
    void updateEntityFromRequest(VariantTypeRequest request, @MappingTarget VariantType variantType);
}