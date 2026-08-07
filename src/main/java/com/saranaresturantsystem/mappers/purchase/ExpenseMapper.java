package com.saranaresturantsystem.mappers.purchase;

import com.saranaresturantsystem.dto.request.purchases.ExpenseRequest;
import com.saranaresturantsystem.dto.response.purchases.ExpenseResponse;
import com.saranaresturantsystem.entities.purchase.Expenses;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    ExpenseResponse toResponse(Expenses expenses);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "bank", ignore = true)
    @Mapping(target = "expenseType", ignore = true)
    Expenses toEntity(ExpenseRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "bank", ignore = true)
    @Mapping(target = "expenseType", ignore = true)
    void updateEntityFromRequest(ExpenseRequest request, @MappingTarget Expenses expenses);
}