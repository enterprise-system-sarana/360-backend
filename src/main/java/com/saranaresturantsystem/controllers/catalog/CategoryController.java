package com.saranaresturantsystem.controllers.catalog;


import com.saranaresturantsystem.common.Message;
import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;

import com.saranaresturantsystem.dto.request.catalog.CategoryRequest;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.dto.response.catalog.CategoryResponse;
import com.saranaresturantsystem.services.interfaces.catalog.CategoryService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
@Tag(name = "Category", description = "Endpoints for managing categories")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Get all categories with pagination
     */
    @GetMapping
//    @PreAuthorize("hasAuthority('category:read')")
    public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam @Parameter(description = """
            Dynamic query parameters.
            Example:
            {
            "name"
            "code"
            "status"
            }
            """) Map<String, String> params) {
        return ResponseFactory.ok(categoryService.findAll(params), "Category");
    }

    /**
     * Get category by ID
     */
    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('category:read')")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Long id) {
        return ResponseFactory.ok(categoryService.getById(id), Message.getById("Category", id));
    }

    /**
     * Create new category
     */
    @PostMapping
//    @PreAuthorize("hasAuthority('category:create')")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseFactory.created(categoryService.save(request), "Category");
    }

    /**
     * Update existing category
     */
    @PutMapping(path = "/{id}")
//    @PreAuthorize("hasAuthority('category:update')")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseFactory.ok(categoryService.update(id, request), Message.updated("Category", id));
    }

    /**
     * Delete category
     */
    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('category:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseFactory.deleted("Category", id);
    }
}
