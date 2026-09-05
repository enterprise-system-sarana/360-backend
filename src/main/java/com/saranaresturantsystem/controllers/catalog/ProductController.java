package com.saranaresturantsystem.controllers.catalog;

import com.saranaresturantsystem.common.Message;
import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.request.catalog.ProductRequest;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.dto.response.catalog.ProductResponse;
import com.saranaresturantsystem.services.interfaces.catalog.ProductService;
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
@RequestMapping("/api/v1/product")
@Tag(name = "Product", description = "Endpoints for managing Product")
public class ProductController {
    private final ProductService productService;

    @GetMapping
//    @PreAuthorize("hasAuthority('product:read')")
    public ResponseEntity<ApiResponse<PageDTO>> getAll(
            @Parameter(description = "Filter params: modelId, name, status")
            @RequestParam Map<String, String> params) {
        return ResponseFactory.ok(productService.findAll(params), "Product");
    }

    /**
     * Get product by ID
     */
    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('product:read')")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id) {
        return ResponseFactory.ok(productService.getById(id), Message.getById("Product", id));
    }

    /**
     * Create new product with file/image upload support
     */
    @PostMapping
//    @PreAuthorize("hasAuthority('product:create')")
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody ProductRequest request) {
        return ResponseFactory.created(productService.create(request), "Product");
    }

    /**
     * Update existing product with file/image upload support
     */
    @PutMapping(path = "/{id}")
//    @PreAuthorize("hasAuthority('product:update')")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseFactory.ok(productService.update(id, request), Message.updated("Product", id));
    }

    /**
     * Delete product
     */
    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('product:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseFactory.deleted("Product", id);
    }
}
