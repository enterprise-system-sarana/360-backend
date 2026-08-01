package com.saranaresturantsystem.services.impl.customer;

import com.saranaresturantsystem.common.UniqueChecker;
import com.saranaresturantsystem.dto.request.customer.CustomerRequest;
import com.saranaresturantsystem.dto.response.customer.CustomerResponse;
import com.saranaresturantsystem.entities.customer.Customer;
import com.saranaresturantsystem.entities.purchase.Suppliers;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.customer.CustomerMapper;
import com.saranaresturantsystem.repository.customer.CustomerRepository;
import com.saranaresturantsystem.services.interfaces.customer.CustomerService;
import com.saranaresturantsystem.specification.customer.CustomerFilter;
import com.saranaresturantsystem.specification.customer.CustomerSpec;
import com.saranaresturantsystem.specification.purchases.supplier.SupplierFilter;
import com.saranaresturantsystem.specification.purchases.supplier.SupplierSpec;
import com.saranaresturantsystem.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final UniqueChecker uniqueChecker;
    private final ObjectMapper objectMapper;
    private final CustomerMapper customerMappers;

    @Override
    public Page<CustomerResponse> findAll(Map<String, String> params) {
        CustomerFilter filter = objectMapper.convertValue(params, CustomerFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Customer> spec = CustomerSpec.filterBy(filter);
        return customerRepository.findAll(spec, pageable).map(customerMappers::toResponse);
    }

    @Override
    public Customer findById(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer:" + id));
        if (customer.getStatus().equals("INACTIVE")) {
            throw new ResourceNotFoundException("Customer:" + id);
        }
        return customer;
    }

    @Override
    public CustomerResponse save(CustomerRequest request) {
        Customer customer = customerMappers.toEntity(request);
        uniqueChecker.verify(customerRepository, customer, "Customer", customer.getName());
        uniqueChecker.verify(customerRepository, customer, "code", customer.getCode());
        customer.setStatus("ACTIVE");
        Customer savedCustomer = customerRepository.save(customer);
        return customerMappers.toResponse(savedCustomer);
    }

    @Override
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = findById(id);
        customerMappers.updateEntityFromRequest(request, customer);
        Customer save = customerRepository.save(customer);
        return customerMappers.toResponse(save);
    }

    @Override
    public CustomerResponse delete(Long id) {
        Customer customer = findById(id);
        customer.setStatus("INACTIVE");
        Customer save=customerRepository.save(customer);
        return customerMappers.toResponse(save);

    }

}
