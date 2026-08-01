package com.saranaresturantsystem.mappers.catalog;

import com.saranaresturantsystem.dto.request.catalog.BrandRequest;
import com.saranaresturantsystem.dto.response.catalog.BrandResponse;
import com.saranaresturantsystem.entities.catalog.Brands;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
@Mapper(componentModel = "spring")
public interface BrandMapper {
    BrandResponse toResponse(Brands  brands);
    @Mapping(target = "id", ignore = true)
    Brands toEntity(BrandRequest request);
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(BrandRequest request, @MappingTarget Brands brands);
}
