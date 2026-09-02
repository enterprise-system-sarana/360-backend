package com.saranaresturantsystem.controllers.purchase;

import com.saranaresturantsystem.common.Message;
import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.request.purchases.PurchaseRequest;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.dto.response.purchases.PurchaseResponse;
import com.saranaresturantsystem.services.interfaces.purchases.PurchaseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/purchases")
@Tag(name = "Purchase", description = "Endpoints for managing purchases and serial numbers")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @GetMapping
    @PreAuthorize("hasAuthority('purchase:read')")
    public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String, String> params) {
        return ResponseFactory.ok(purchaseService.findAll(params), "Purchase");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('purchase:read')")
    public ResponseEntity<ApiResponse<PurchaseResponse>> getById(@PathVariable Long id) {
        return ResponseFactory.ok(purchaseService.findByIdResponse(id), Message.getById("Purchase", id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('purchase:create')")
    public ResponseEntity<ApiResponse<PurchaseResponse>> create(@Valid @RequestBody PurchaseRequest request) {
        return ResponseFactory.created(purchaseService.save(request), "Purchase");
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('purchase:update')")
    public ResponseEntity<ApiResponse<PurchaseResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseRequest request) {
        return ResponseFactory.ok(purchaseService.update(id, request), Message.updated("Purchase", id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('purchase:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        purchaseService.delete(id);
        return ResponseFactory.deleted("Purchase", id);
    }
}