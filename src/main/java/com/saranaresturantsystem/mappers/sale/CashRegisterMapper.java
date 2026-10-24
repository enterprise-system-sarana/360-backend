package com.saranaresturantsystem.mappers.sale;

import com.saranaresturantsystem.dto.response.sales.CashRegisterResponse;
import com.saranaresturantsystem.entities.sales.CashRegister;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class CashRegisterMapper {

    @Mapping(source = "store.id", target = "storeId")
    @Mapping(source = "store.name", target = "storeName")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "userName")
    @Mapping(source = "user.profileImage", target = "userProfileImage")
    public abstract CashRegisterResponse toResponse(CashRegister cashRegister);
}
