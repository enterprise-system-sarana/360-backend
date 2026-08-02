package com.saranaresturantsystem.mappers.catalog;

import com.saranaresturantsystem.dto.request.catalog.VariantValueRequest;
import com.saranaresturantsystem.dto.response.catalog.VariantValueResponse;
import com.saranaresturantsystem.entities.catalog.VariantValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VariantValueMapper {

    @Mapping(target = "variantTypeName", source = "variantType.name")
    VariantValueResponse toResponse(VariantValue variantValue);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "variantType", ignore = true)
    VariantValue toEntity(VariantValueRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "variantType", ignore = true)
    void updateEntityFromRequest(VariantValueRequest request, @MappingTarget VariantValue variantValue);
}