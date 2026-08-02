package com.saranaresturantsystem.mappers.catalog;

import com.saranaresturantsystem.dto.request.catalog.VariantTypeRequest;
import com.saranaresturantsystem.dto.response.catalog.VariantTypeResponse;
import com.saranaresturantsystem.entities.catalog.VariantType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VariantTypeMapper {
    VariantTypeResponse toResponse(VariantType variantType);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    VariantType toEntity(VariantTypeRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntityFromRequest(VariantTypeRequest request, @MappingTarget VariantType variantType);
}