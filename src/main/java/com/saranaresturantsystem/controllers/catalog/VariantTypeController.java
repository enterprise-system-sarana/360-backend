package com.saranaresturantsystem.controllers.catalog;

import com.saranaresturantsystem.common.Message;
import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.request.catalog.VariantTypeRequest;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.dto.response.catalog.VariantTypeResponse;
import com.saranaresturantsystem.services.interfaces.catalog.VariantTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/variant-types")
@Tag(name = "Variant Type", description = "Endpoints for managing variant types")
public class VariantTypeController {

    private final VariantTypeService variantTypeService;

    /**
     * Get all variant types with pagination
     */
    @GetMapping
    @PreAuthorize("hasAuthority('variantType:read')")
    public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String, String> params) {
        return ResponseFactory.ok(variantTypeService.findAll(params), "VariantType");
    }

    /**
     * Create new variant type
     */
    @PostMapping
    @PreAuthorize("hasAuthority('variantType:create')")
    public ResponseEntity<ApiResponse<VariantTypeResponse>> create(@Valid @RequestBody VariantTypeRequest request) {
        return ResponseFactory.created(variantTypeService.save(request), "VariantType");
    }

    /**
     * Update existing variant type
     */
    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('variantType:update')")
    public ResponseEntity<ApiResponse<VariantTypeResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody VariantTypeRequest request) {
        return ResponseFactory.ok(variantTypeService.update(id, request), Message.updated("VariantType", id));
    }

    /**
     * Delete variant type
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('variantType:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        variantTypeService.delete(id);
        return ResponseFactory.deleted("VariantType", id);
    }
}