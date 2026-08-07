//package com.saranaresturantsystem.controllers.catalog;
//
//import com.saranaresturantsystem.common.Message;
//import com.saranaresturantsystem.common.ResponseFactory;
//import com.saranaresturantsystem.dto.PageDTO;
//import com.saranaresturantsystem.dto.request.catalog.ProductVariantRequest;
//import com.saranaresturantsystem.dto.response.ApiResponse;
//import com.saranaresturantsystem.dto.response.catalog.ProductVariantResponse;
//import com.saranaresturantsystem.services.interfaces.catalog.ProductVariantService;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/product-variant")
//@Tag(name = "ProductVariant", description = "Endpoints for managing Product variant")
//public class ProductVariantController {
//    private  final ProductVariantService variantService;
//    @GetMapping
////    @PreAuthorize("hasAuthority('brand:read')")
//    public ResponseEntity<ApiResponse<PageDTO>> getAll(
//            @Parameter(description = "Filter params: modelId, name, status")
//            @RequestParam Map<String, String> params) {
//        return ResponseFactory.ok(variantService.findAll(params), "Product Variant");
//    }
//
//    /**
//     * Get brand by ID
//     */
//    @GetMapping("/{id}")
////    @PreAuthorize("hasAuthority('brand:read')")
//    public ResponseEntity<ApiResponse<ProductVariantResponse>> getById(@PathVariable Long id) {
//        return ResponseFactory.ok(variantService.getById(id), Message.getById("Product variant", id));
//    }
//
//    /**
//     * Create new brand with file/image upload support
//     */
//    @PostMapping
////    @PreAuthorize("hasAuthority('brand:create')")
//    public ResponseEntity<ApiResponse<ProductVariantResponse>> create(@Valid @RequestBody ProductVariantRequest request) {
//        return ResponseFactory.created(variantService.create(request), "Product Variant ");
//    }
//
//    /**
//     * Update existing brand with file/image upload support
//     */
//    @PutMapping(path = "/{id}")
////    @PreAuthorize("hasAuthority('brand:update')")
//    public ResponseEntity<ApiResponse<ProductVariantResponse>> update(
//            @PathVariable Long id,
//            @Valid @RequestBody ProductVariantRequest request) {
//        return ResponseFactory.ok(variantService.update(id, request), Message.updated("Product variant ", id));
//    }
//
//    /**
//     * Delete brand
//     */
//    @DeleteMapping("/{id}")
////    @PreAuthorize("hasAuthority('brand:delete')")
//    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
//        variantService.delete(id);
//        return ResponseFactory.deleted("Product variant ", id);
//    }
//}
