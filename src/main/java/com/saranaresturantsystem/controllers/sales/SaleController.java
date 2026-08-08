package com.saranaresturantsystem.controllers.sales;

import com.saranaresturantsystem.common.Message;
import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.request.sales.SaleRequest;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.dto.response.sales.SaleResponse;
import com.saranaresturantsystem.enums.SaleStatus;
import com.saranaresturantsystem.services.interfaces.sales.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sales")
@Tag(name = "Sale", description = "Endpoints for managing sales, stock deduction, and customer transactions")
public class SaleController {
    private final SaleService salesService;

    @PostMapping
//    @PreAuthorize("hasAuthority('sale:create')")
    @Operation(summary = "Create sale, deduct stock, and record transaction")
    public ResponseEntity<ApiResponse<SaleResponse>> create(@Valid @RequestBody SaleRequest request,
                                                            Principal principal) {
        String createdBy = principal != null ? principal.getName() : "system";
        return ResponseFactory.created(salesService.create(request, createdBy), "Sale");
    }

    @GetMapping
//    @PreAuthorize("hasAuthority('sale:read')")
    @Operation(summary = "Get list of sales with pagination and filters")
    public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String, String> params) {
        Page<SaleResponse> page = salesService.getAll(params);
        return ResponseFactory.ok(page, "Sale");
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('sale:read')")
    @Operation(summary = "Get sale details by ID")
    public ResponseEntity<ApiResponse<SaleResponse>> getById(@PathVariable Long id) {
        return ResponseFactory.ok(salesService.getById(id), Message.getById("Sale", id));
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('sale:update')")
    @Operation(summary = "Update sale details (Only PENDING sales)")
    public ResponseEntity<ApiResponse<SaleResponse>> update(@PathVariable Long id,
                                                            @Valid @RequestBody SaleRequest request, Principal principal) {
        String updatedBy = principal != null ? principal.getName() : "system";
        return ResponseFactory.ok(salesService.update(id, request, updatedBy), Message.updated("Sale", id));
    }


    @PatchMapping("/{id}/complete")
//    @PreAuthorize("hasAuthority('sale:update')")
    @Operation(summary = "Complete sale transaction and deduct inventory stock")
    public ResponseEntity<ApiResponse<SaleResponse>> complete(@PathVariable Long id, Principal principal) {
        String updatedBy = principal != null ? principal.getName() : "system";
        return ResponseFactory.ok(salesService.complete(id, updatedBy), "Sale completed successfully");
    }

    @PatchMapping("/{id}/cancel")
//    @PreAuthorize("hasAuthority('sale:update')")
    @Operation(summary = "Cancel sale transaction and restore inventory stock")
    public ResponseEntity<ApiResponse<SaleResponse>> cancel(@PathVariable Long id, Principal principal) {
        String updatedBy = principal != null ? principal.getName() : "system";
        return ResponseFactory.ok(salesService.cancel(id, updatedBy), "Sale cancelled successfully");
    }

    @PatchMapping("/{id}/return")
//    @PreAuthorize("hasAuthority('sale:update')")
    @Operation(summary = "Return sale transaction and restore inventory stock")
    public ResponseEntity<ApiResponse<SaleResponse>> returnSale(@PathVariable Long id, Principal principal) {
        String updatedBy = principal != null ? principal.getName() : "system";
        return ResponseFactory.ok(salesService.returnSale(id, updatedBy), "Sale returned successfully");
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('sale:delete')")
    @Operation(summary = "Soft delete a sale record")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Principal principal) {
        String deletedBy = principal != null ? principal.getName() : "system";
        salesService.delete(id, deletedBy);
        return ResponseFactory.deleted("Sale", id);
    }
}
