package com.saranaresturantsystem.mappers.sale;

import com.saranaresturantsystem.dto.request.sales.PaymentRequest;
import com.saranaresturantsystem.dto.response.sales.PaymentResponse;
import com.saranaresturantsystem.entities.sales.Payment;
import com.saranaresturantsystem.services.interfaces.finances.BankService;
import com.saranaresturantsystem.services.interfaces.purchases.PurchaseService;
import com.saranaresturantsystem.services.interfaces.sales.SaleService;
import com.saranaresturantsystem.services.interfaces.users.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring" , uses = {BankService.class , PurchaseService.class, SaleService.class , UserService.class})
public interface PaymentMapper {

    @Mapping(source = "sales.id", target = "saleId")
    @Mapping(source = "sales.no", target = "saleNo")
    @Mapping(source = "purchase.id", target = "purchaseId")
    @Mapping(source = "banks.id", target = "bankId")
    @Mapping(source = "banks.name", target = "bankName")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "userName")
    @Mapping(source = "paymentNo", target = "paymentNo")
    @Mapping(source = "paymentDate", target = "paymentDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    PaymentResponse toResponse(Payment payment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sales", ignore = true)
    @Mapping(target = "purchase", ignore = true)
    @Mapping(target = "banks", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "paymentNo", source = "paymentNo")
    @Mapping(target = "paymentDate", source = "paymentDate")
    Payment toEntity(PaymentRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sales", ignore = true)
    @Mapping(target = "purchase", ignore = true)
    @Mapping(target = "banks", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "paymentNo", source = "paymentNo")
    @Mapping(target = "paymentDate", source = "paymentDate")
    void updateFromRequest(PaymentRequest request, @MappingTarget Payment payment);
}

