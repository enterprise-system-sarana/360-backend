package com.saranaresturantsystem.controllers.catalog;

import com.saranaresturantsystem.common.Message;
import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.request.catalog.BrandRequest;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.dto.response.catalog.BrandResponse;
import com.saranaresturantsystem.services.interfaces.catalog.BrandService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/brand")
@Tag(name = "Brand", description = "Endpoints for managing brands")
public class BrandController {

    private final BrandService brandService;

    /**
     * Get all brands with pagination
     */
    @GetMapping
//    @PreAuthorize("hasAuthority('brand:read')")
    public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String, String> params) {
        return ResponseFactory.ok(brandService.findAll(params), "Brand");
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
    public ResponseEntity<ApiResponse<BrandResponse>> create(@Valid @RequestBody BrandRequest request) {
        return ResponseFactory.created(brandService.save(request), "Brand");
    }

    /**
     * Update existing brand with file/image upload support
     */
    @PutMapping(path = "/{id}")
//    @PreAuthorize("hasAuthority('brand:update')")
    public ResponseEntity<ApiResponse<BrandResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody BrandRequest request) {
        return ResponseFactory.ok(brandService.update(id, request), Message.updated("Brand", id));
    }

    /**
     * Delete brand
     */
    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('brand:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        brandService.delete(id);
        return ResponseFactory.deleted("Brand", id);
    }
}