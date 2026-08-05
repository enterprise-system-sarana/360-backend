package com.saranaresturantsystem.services.interfaces.customer;

import com.saranaresturantsystem.dto.request.customer.CustomerRequest;
import com.saranaresturantsystem.dto.response.customer.CustomerResponse;
import com.saranaresturantsystem.dto.response.purchases.SupplierResponse;
import com.saranaresturantsystem.entities.customer.Customer;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface CustomerService {
    Page<CustomerResponse> findAll(Map<String, String> params);

    Customer findById(Long id);

    CustomerResponse save(CustomerRequest request);

    CustomerResponse update(Long id, CustomerRequest request);

    CustomerResponse delete(Long id);
}
