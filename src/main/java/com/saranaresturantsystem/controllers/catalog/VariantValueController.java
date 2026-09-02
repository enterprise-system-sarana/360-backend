package com.saranaresturantsystem.controllers.catalog;

import com.saranaresturantsystem.common.Message;
import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.request.catalog.VariantValueRequest;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.dto.response.catalog.VariantValueResponse;
import com.saranaresturantsystem.services.interfaces.catalog.VariantValueService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/variant-values")
@Tag(name = "Variant Value", description = "Endpoints for managing variant values")
public class VariantValueController {

    private final VariantValueService variantValueService;

    /**
     * Get all variant values with pagination
     */
    @GetMapping
    @PreAuthorize("hasAuthority('variantValue:read')")
    public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String, String> params) {
        return ResponseFactory.ok(variantValueService.findAll(params), "VariantValue");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('variantValue:read')")
    public  ResponseEntity<ApiResponse<VariantValueResponse>> getById(@PathVariable Long id) {
        return ResponseFactory.ok(variantValueService.getById(id), "variantValue");
    }
    /**
     * Create new variant value
     */
    @PostMapping
    @PreAuthorize("hasAuthority('variantValue:create')")
    public ResponseEntity<ApiResponse<VariantValueResponse>> create(@Valid @RequestBody VariantValueRequest request) {
        return ResponseFactory.created(variantValueService.save(request), "VariantValue");
    }

    /**
     * Update existing variant value
     */
    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('variantValue:update')")
    public ResponseEntity<ApiResponse<VariantValueResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody VariantValueRequest request) {
        return ResponseFactory.ok(variantValueService.update(id, request), Message.updated("VariantValue", id));
    }

    /**
     * Delete variant value
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('variantValue:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        variantValueService.delete(id);
        return ResponseFactory.deleted("VariantValue", id);
    }
}