package com.saranaresturantsystem.controllers.catalog;

import com.saranaresturantsystem.common.Message;
import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.request.catalog.ModelRequest;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.dto.response.catalog.ModelResponse;
import com.saranaresturantsystem.services.interfaces.catalog.ModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Model", description = "Endpoints for managing restaurant products and inventory")
@RequestMapping("/api/v1/model")
public class ModelController {

    private final ModelService modelService;

    /**
     * Get all products with pagination and filters
     */
    @GetMapping
    @Operation(summary = "Get all products with pagination and filters")
    @PreAuthorize("hasAuthority('model:read')")
    public ResponseEntity<ApiResponse<PageDTO>> getList(
            @Parameter(description = "Filter params: brandId, name, status, categoryId") @RequestParam Map<String, String> params) {
        return ResponseFactory.ok(modelService.findAll(params), "Model");
    }

    /**
     * Find a product by its ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Find a product by its ID")
    @PreAuthorize("hasAuthority('model:read')")
    public ResponseEntity<ApiResponse<ModelResponse>> getById(@PathVariable Long id) {
        return ResponseFactory.ok(modelService.getById(id), Message.getById("Model", id));
    }

    /**
     * Create a new product with an optional image
     */
    @PostMapping()
    @Operation(summary = "Create a new product with an optional image")
    @PreAuthorize("hasAuthority('model:create')")
    public ResponseEntity<ApiResponse<ModelResponse>> create(
            @Valid @RequestBody ModelRequest request) {
        return ResponseFactory.created(modelService.save(request), "Model");
    }

    /**
     * Update product details; send a new image file to replace the existing one
     */
    @PutMapping(value = "/{id}")
    @Operation(summary = "Update product details; send a new image file to replace the existing one")
    @PreAuthorize("hasAuthority('model:update')")
    public ResponseEntity<ApiResponse<ModelResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ModelRequest request) {
        return ResponseFactory.ok(modelService.update(id, request), Message.updated("Model", id));
    }

    /**
     * Soft-delete a product by setting its status to INACTIVE
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Soft-delete a product by setting its status to INACTIVE")
    @PreAuthorize("hasAuthority('model:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        modelService.delete(id);
        return ResponseFactory.deleted("Model", id);
    }
}
