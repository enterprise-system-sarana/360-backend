package com.saranaresturantsystem.controllers.inventory;

import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.services.interfaces.inventory.StockService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageDTO>> findAll(
            @RequestParam
            @Parameter(description = """
                     Dynamic query parameters.
                     Example:
                    {
                    "productId"
                     "storeId"
                    
                     }
                    """) Map<String, String> params) {
        return ResponseFactory.ok(stockService.findAll(params), "Stock");
    }
}
