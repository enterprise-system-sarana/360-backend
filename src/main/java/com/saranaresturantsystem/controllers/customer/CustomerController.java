package com.saranaresturantsystem.controllers.customer;

import com.saranaresturantsystem.common.Message;
import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.request.customer.CustomerRequest;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.dto.response.customer.CustomerResponse;
import com.saranaresturantsystem.services.interfaces.customer.CustomerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/customer")
@Tag(name="Customer",description = "Endpoints for managing customer")

public class CustomerController{
    private final CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasAuthority('customer:read')")
    public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String, String> params) {
        return ResponseFactory.ok(customerService.findAll(params), "Customer");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('customer:read')")
    public  ResponseEntity<ApiResponse<CustomerResponse>> getById(@PathVariable Long id) {
        return ResponseFactory.ok(customerService.getById(id), "Customer");
    }
    @PostMapping
    @PreAuthorize("hasAuthority('customer:create')")
    public ResponseEntity<ApiResponse<CustomerResponse>> create(@Valid @RequestBody CustomerRequest request) {
        return ResponseFactory.created(customerService.save(request), "Customer");
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('customer:update')")
    public ResponseEntity<ApiResponse<CustomerResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request) {
        return ResponseFactory.ok(customerService.update(id, request), Message.updated("Customer", id));
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('customer:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseFactory.deleted("Customer", id);
    }



}
