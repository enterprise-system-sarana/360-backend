package com.saranaresturantsystem.mappers.purchase;

import com.saranaresturantsystem.dto.request.purchases.SupplierRequest;
import com.saranaresturantsystem.dto.response.purchases.SupplierResponse;
import com.saranaresturantsystem.entities.purchase.Suppliers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
     SupplierResponse toResponse(Suppliers suppliers);
    @Mapping(target = "id", ignore = true)
    Suppliers toEntity(SupplierRequest request);
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(SupplierRequest request, @MappingTarget Suppliers suppliers);
}
