package com.saranaresturantsystem.controllers.purchase;


import com.saranaresturantsystem.common.Message;
import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.request.purchases.SupplierRequest;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.dto.response.purchases.SupplierResponse;
import com.saranaresturantsystem.services.interfaces.purchases.SupplierService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/suppliers")
@Tag(name = "Supplier", description = "Endpoints for managing suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    /**
     * Get all suppliers with pagination
     */
    @GetMapping
    @PreAuthorize("hasAuthority('supplier:read')")
    public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String, String> params) {
        return ResponseFactory.ok(supplierService.findAll(params), "Supplier");
    }

    /**
     * Get supplier by ID
     */
//    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('supplier:read')")
//    public ResponseEntity<ApiResponse<SupplierResponse>> getById(@PathVariable Long id) {
//        return ResponseFactory.ok(supplierService.findById(id), Message.getById("Supplier", id));
//    }

    /**
     * Create new supplier
     */
    @PostMapping
    @PreAuthorize("hasAuthority('supplier:create')")
    public ResponseEntity<ApiResponse<SupplierResponse>> create(@Valid @ModelAttribute SupplierRequest request) {
        return ResponseFactory.created(supplierService.save(request), "Supplier");
    }

    /**
     * Update existing supplier
     */
    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('supplier:update')")
    public ResponseEntity<ApiResponse<SupplierResponse>> update(
            @PathVariable Long id,
            @Valid @ModelAttribute SupplierRequest request) {
        return ResponseFactory.ok(supplierService.update(id, request), Message.updated("Supplier", id));
    }

    /**
     * Delete supplier
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('supplier:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return ResponseFactory.deleted("Supplier", id);
    }
}
