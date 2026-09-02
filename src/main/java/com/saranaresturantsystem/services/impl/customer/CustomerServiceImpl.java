package com.saranaresturantsystem.services.impl.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.common.UniqueChecker;
import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.dto.request.customer.CustomerRequest;
import com.saranaresturantsystem.dto.response.customer.CustomerResponse;
import com.saranaresturantsystem.entities.customer.Customer;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.customer.CustomerMapper;
import com.saranaresturantsystem.repository.customer.CustomerRepository;
import com.saranaresturantsystem.services.interfaces.customer.CustomerService;
import com.saranaresturantsystem.specification.customer.CustomerFilter;
import com.saranaresturantsystem.specification.customer.CustomerSpec;

import com.saranaresturantsystem.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

//    @Cacheable(value = "customers", key = "#id")
    @Override
    public Customer findById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer:" + id));
        if (customer.getStatus().equals(Constants.STATUS_INIT) || customer.getStatus().equals(Constants.STATUS_DELETE)) {
            throw new ResourceNotFoundException("Customer:" + id);
        }
        return customer;
    }

    @Override
    public CustomerResponse getById(Long id) {
        return  customerMappers.toResponse(findById(id));
    }

    @Override
    public CustomerResponse save(CustomerRequest request) {
        Customer customer = customerMappers.toEntity(request);
        customer.setStatus(Constants.STATUS_ACTIVE);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMappers.toResponse(savedCustomer);
    }

//    @CacheEvict(value = "customers", key = "#id")
    @Override
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = findById(id);
        customerMappers.updateEntityFromRequest(request, customer);
        Customer save = customerRepository.save(customer);
        return customerMappers.toResponse(save);
    }

//    @CacheEvict(value = "customers", key = "#id")
    @Override
    public CustomerResponse delete(Long id) {
        Customer customer = findById(id);
        customer.setStatus(Constants.STATUS_DELETE);
        Customer save = customerRepository.save(customer);
        return customerMappers.toResponse(save);

    }

}
