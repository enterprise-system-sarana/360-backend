package com.saranaresturantsystem.controllers.finances;


import com.saranaresturantsystem.common.Message;
import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.request.catalog.CategoryRequest;
import com.saranaresturantsystem.dto.request.finances.CurrencyRequest;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.dto.response.catalog.CategoryResponse;
import com.saranaresturantsystem.dto.response.finances.CurrencyResponse;
import com.saranaresturantsystem.services.interfaces.finances.CurrencyService;
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
@RequestMapping("/api/v1/currency")
@Tag(name = "Currency", description = "Endpoints for managing Currency")
public class CurrencyController {
    private  final CurrencyService currencyService ;
    @GetMapping
    @PreAuthorize("hasAuthority('currency:read')")
    public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam @Parameter(description = """
            Dynamic query parameters.
            Example:
            {
            "name"
            "code"
            "status"
            }
            """) Map<String, String> params) {
        return ResponseFactory.ok(currencyService.findAll(params), "Currency");
    }

    /**
     * Get Currency by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('currency:read')")
    public ResponseEntity<ApiResponse<CurrencyResponse>> getById(@PathVariable Long id) {
        return ResponseFactory.ok(currencyService.getById(id), Message.getById("Currency", id));
    }

    /**
     * Create new Currency
     */
    @PostMapping
    @PreAuthorize("hasAuthority('currency:create')")
    public ResponseEntity<ApiResponse<CurrencyResponse>> create(@Valid @RequestBody CurrencyRequest request) {
        return ResponseFactory.created(currencyService.save(request), "Currency");
    }

    /**
     * Update existing Currency
     */
    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('currency:update')")
    public ResponseEntity<ApiResponse<CurrencyResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CurrencyRequest request) {
        return ResponseFactory.ok(currencyService.update(request, id), Message.updated("Currency", id));
    }

    /**
     * Delete Currency
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('currency:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        currencyService.delete(id);
        return ResponseFactory.deleted("Category", id);
    }
}

