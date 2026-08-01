package com.saranaresturantsystem.mappers.inventory;

import com.saranaresturantsystem.dto.request.inventory.StoreRequest;
import com.saranaresturantsystem.dto.response.inventory.StoreResponse;
import com.saranaresturantsystem.entities.Stores;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StoreMapper {
    StoreResponse toResponse (Stores stores );
    @Mapping(target = "id", ignore = true)
    Stores toEntity (StoreRequest request);
    @Mapping(target = "id", ignore = true)
    void updateEnityFromRequest (StoreRequest request, @MappingTarget Stores stores);
}
