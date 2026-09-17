package com.saranaresturantsystem.reports.service.customer;

import com.saranaresturantsystem.entities.customer.Customer;
import com.saranaresturantsystem.reports.dto.customers.CustomerReportResponse;
import com.saranaresturantsystem.reports.specification.customers.CustomerReportFilter;
import com.saranaresturantsystem.reports.specification.customers.CustomerReportSpec;
import com.saranaresturantsystem.repository.customer.CustomerRepository; // Adjust to your actual customer repository package
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerReportServiceImpl implements CustomerReportService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerReportResponse> getCustomersReport(CustomerReportFilter filter, Pageable pageable) {
        Specification<Customer> spec = CustomerReportSpec.filter(filter);

        return customerRepository.findAll(spec, pageable).map(customer ->
                CustomerReportResponse.builder()
                        .id(customer.getId())
                        .name(customer.getName())
                        .code(customer.getCode())
                        .phone(customer.getPhone())
                        .email(customer.getEmail())
                        .note(customer.getNote())
                        .status(customer.getStatus())
                        .build()
        );
    }
}