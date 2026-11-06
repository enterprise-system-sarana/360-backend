package com.saranaresturantsystem.mappers.finances;

import com.saranaresturantsystem.dto.request.finances.BankRequest;
import com.saranaresturantsystem.dto.response.finances.BankResponse;
import com.saranaresturantsystem.entities.finances.Banks;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BankMapper {
    BankResponse toBankResponse(Banks bank);

    @Mapping(target = "id", ignore = true)
    Banks toEntity(BankRequest bankRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(BankRequest bankRequest, @MappingTarget Banks bank);
}
