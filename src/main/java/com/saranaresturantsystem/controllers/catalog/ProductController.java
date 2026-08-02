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
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
@Tag(name = "Product", description = "Endpoints for managing Product")
public class ProductController {
    private  final ProductService productService ;
    @GetMapping
//    @PreAuthorize("hasAuthority('brand:read')")
    public ResponseEntity<ApiResponse<PageDTO>> getAll(
            @Parameter(description = "Filter params: modelId, name, status")
            @RequestParam Map<String, String> params) {
        return ResponseFactory.ok(productService.findAll(params), "Product");
    }

    /**
     * Get brand by ID
     */
//    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('brand:read')")
//    public ResponseEntity<ApiResponse<BrandResponse>> getById(@PathVariable Long id) {
//        return ResponseFactory.ok(brandService.findById(id), Message.getById("Brand", id));
//    }

    /**
     * Create new brand with file/image upload support
     */
    @PostMapping
//    @PreAuthorize("hasAuthority('brand:create')")
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody ProductRequest request) {
        return ResponseFactory.created(productService.create(request), "Product");
    }

    /**
     * Update existing brand with file/image upload support
     */
    @PutMapping(path = "/{id}")
//    @PreAuthorize("hasAuthority('brand:update')")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseFactory.ok(productService.update(id, request), Message.updated("Product", id));
    }

    /**
     * Delete brand
     */
    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('brand:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseFactory.deleted("Product", id);
    }
}
