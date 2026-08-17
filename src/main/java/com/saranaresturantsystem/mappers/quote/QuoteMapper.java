package com.saranaresturantsystem.mappers.quote;

import com.saranaresturantsystem.dto.request.customer.CustomerRequest;
import com.saranaresturantsystem.dto.request.quote.QuoteRequest;
import com.saranaresturantsystem.dto.response.customer.CustomerResponse;
import com.saranaresturantsystem.dto.response.quote.QuoteItemResponse;
import com.saranaresturantsystem.dto.response.quote.QuoteResponse;
import com.saranaresturantsystem.entities.customer.Customer;
import com.saranaresturantsystem.entities.quote.Quote;
import com.saranaresturantsystem.entities.quote.QuoteItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface QuoteMapper {
    // ១. Map ពី Quote Entity ទៅ QuoteResponse DTO
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "items", source = "quoteItems")
    QuoteResponse toResponse(Quote quote);

    // ២. Map ពី QuoteItem Entity ទៅ QuoteItemResponse DTO
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "product_serial", source = "product_serials.id")
    QuoteItemResponse toItemResponse(QuoteItem item);

    @BeanMapping(ignoreByDefault = true)
    Customer toCustomerEntity(QuoteRequest request);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "quoteItems", ignore = true)
    void updateEntityFromRequest(QuoteRequest request, @MappingTarget Quote quote);
}
