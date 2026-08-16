package com.saranaresturantsystem.mappers.catalog;

import com.saranaresturantsystem.dto.request.catalog.VariantValueRequest;
import com.saranaresturantsystem.dto.response.catalog.VariantValueResponse;
import com.saranaresturantsystem.entities.catalog.VariantValue;
import com.saranaresturantsystem.services.interfaces.catalog.VariantTypeService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring" , uses = {VariantTypeService.class})
public interface VariantValueMapper {


    @Mapping(target = "variantTypeName", source = "variantType.name")
    @Mapping(target = "variantTypeId", source = "variantType.id")
    VariantValueResponse toResponse(VariantValue variantValue);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "variantType", source = "variantTypeId")
    VariantValue toEntity(VariantValueRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "variantType", source = "variantTypeId")
    void updateEntityFromRequest(VariantValueRequest request, @MappingTarget VariantValue variantValue);
}