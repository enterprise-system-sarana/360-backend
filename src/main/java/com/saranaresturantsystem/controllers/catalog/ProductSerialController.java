package com.saranaresturantsystem.controllers.catalog;

import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.services.interfaces.catalog.ProductSerialService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product-serial")
@Tag(name = "product serial ", description = "Endpoints for managing product serial ")
public class ProductSerialController {
    private  final ProductSerialService productSerialService ;

    @GetMapping
//    @PreAuthorize("hasAuthority('category:read')")
    public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam @Parameter(description = """
            Dynamic query parameters.
            Example:
            {
            "barCode"
            "productId"
//            "storeId"
            "status"
            }
            """) Map<String, String> params) {
        return ResponseFactory.ok(productSerialService.findAll(params), "ProductSerial");
    }

}
