package com.saranaresturantsystem.mappers.finances;

import com.saranaresturantsystem.dto.request.finances.CurrencyRequest;
import com.saranaresturantsystem.dto.response.finances.CurrencyResponse;
import com.saranaresturantsystem.entities.finances.Currency;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {
    CurrencyResponse toResponse(Currency currency);

    @Mapping(target = "id", ignore = true)
    Currency toEntity (CurrencyRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(CurrencyRequest currencyRequest , @MappingTarget Currency entity);
}
