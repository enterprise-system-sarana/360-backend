package com.saranaresturantsystem.mappers.catalog;

import com.saranaresturantsystem.dto.request.catalog.ProductRequest;
import com.saranaresturantsystem.dto.response.catalog.ProductResponse;
import com.saranaresturantsystem.entities.catalog.Product;
import com.saranaresturantsystem.services.interfaces.catalog.ModelService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring" , uses = {ModelService.class})
public interface ProductMapper {

    @Mapping(target = "modelId" ,source = "models.id")
    @Mapping(target = "modelName" ,source = "models.name")
    ProductResponse toResponse (Product product);
    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "models" , source = "modelId")
    Product toEntity(ProductRequest request);
    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "models" , source = "modelId")
    void updateEntityFromRequest(ProductRequest request , @MappingTarget Product product);

}
