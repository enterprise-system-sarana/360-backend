package com.saranaresturantsystem.controllers.quote;

import com.saranaresturantsystem.common.Message;
import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.request.catalog.BrandRequest;
import com.saranaresturantsystem.dto.request.quote.QuoteRequest;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.dto.response.catalog.BrandResponse;
import com.saranaresturantsystem.dto.response.quote.QuoteResponse;
import com.saranaresturantsystem.services.interfaces.quote.QuoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/quote")
@Tag(name = "Quote", description = "Endpoints for managing quote")
public class QuoteController {
    private final QuoteService quoteService;
    @GetMapping
//    @PreAuthorize("hasAuthority('adjustment:read')")
    public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String, String> params) {
        return ResponseFactory.ok(quoteService.getList(params), "Quote");
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('adjustment:read')")
    public ResponseEntity<ApiResponse<QuoteResponse>> getById(@Valid @PathVariable Long id) {
        return ResponseFactory.ok(quoteService.findById(id), Message.getById("Quote", id));
    }

    @PostMapping
//    @PreAuthorize("hasAuthority('adjustment:create')")
    public ResponseEntity<ApiResponse<QuoteResponse>> create(@Valid @RequestBody QuoteRequest request) {
        return ResponseFactory.created(quoteService.createQuote(request), "Quote");
    }
//    @PutMapping(path = "/{id}")
////    @PreAuthorize("hasAuthority('brand:update')")
//    public ResponseEntity<ApiResponse<BrandResponse>> update(
//            @PathVariable Long id,
//            @Valid @RequestBody BrandRequest request) {
//        return ResponseFactory.ok(brandService.update(id, request), Message.updated("Brand", id));
//    }
@PutMapping("/{id}")
public ResponseEntity<ApiResponse<QuoteResponse>> update(
        @PathVariable Long id,
        @Valid @RequestBody QuoteRequest request) {
    return ResponseFactory.created(quoteService.update(id, request), Message.updated("Quote",id));

}

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('adjustment:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        quoteService.deleteQuote(id);
        return ResponseFactory.deleted("Quote", id);
    }
}
