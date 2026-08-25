package com.saranaresturantsystem.mappers.purchase;

import com.saranaresturantsystem.dto.request.purchases.ExpenseRequest;
import com.saranaresturantsystem.dto.response.purchases.ExpenseResponse;
import com.saranaresturantsystem.entities.purchase.Expenses;
import com.saranaresturantsystem.services.interfaces.finances.BankService;
import com.saranaresturantsystem.services.interfaces.inventory.StoreService;
import com.saranaresturantsystem.services.interfaces.purchases.ExpenseTypeService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring" , uses = {StoreService.class , ExpenseTypeService.class , BankService.class})
public interface ExpenseMapper {

    @Mapping(target = "bankId" , source = "bank.id")
    @Mapping(target = "bankName" , source = "bank.name")
    @Mapping(target = "expenseTypeId" , source = "expenseType.id")
    @Mapping(target = "expenseTypeName" , source = "expenseType.name")
    @Mapping(target = "storeId" , source = "stores.id")
    @Mapping(target = "storeName" , source = "stores.name")
    ExpenseResponse toResponse(Expenses expenses);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "bank", source = "bankId")
    @Mapping(target = "expenseType", source = "expenseTypeId")
    @Mapping(target = "stores", source = "storeId")
    Expenses toEntity(ExpenseRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "bank", source = "bankId")
    @Mapping(target = "expenseType", source = "expenseTypeId")
    @Mapping(target = "stores" , source = "storeId")
    void updateEntityFromRequest(ExpenseRequest request, @MappingTarget Expenses expenses);
}