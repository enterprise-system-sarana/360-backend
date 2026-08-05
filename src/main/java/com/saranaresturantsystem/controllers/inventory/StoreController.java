package com.saranaresturantsystem.controllers.inventory;

import com.saranaresturantsystem.common.Message;
import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.request.inventory.StoreRequest;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.dto.response.inventory.StoreResponse;
import com.saranaresturantsystem.services.interfaces.inventory.StoreService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
@Tag(name = "Store", description = "Endpoints for managing stores")
public class StoreController {
    private final StoreService storeService;

    @GetMapping
//    @PreAuthorize("hasAuthority('store:read')")
    public ResponseEntity<ApiResponse<PageDTO>> getAll(
            @RequestParam
            @Parameter(description = """
             Dynamic query parameters.
             Example:
            {
            "name"
             "code"
            "email"
            "phone"
            "city"
            "status"
             }
                 """) Map<String, String> params) {
        return ResponseFactory.ok(storeService.findAll(params), "Store");
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('store:read')")
    public ResponseEntity<ApiResponse<StoreResponse>> getById(@Positive @PathVariable Long id) {
        return ResponseFactory.ok(storeService.getById(id), Message.getById("Store", id));
    }

    @PostMapping
//    @PreAuthorize("hasAuthority('store:create')")
    public ResponseEntity<ApiResponse<StoreResponse>> create(@Valid @RequestBody StoreRequest request) {
        return ResponseFactory.created(storeService.save(request), "Store");
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('store:update')")
    public ResponseEntity<ApiResponse<StoreResponse>> update(@PathVariable Long id, @Valid @RequestBody StoreRequest request) {
        return ResponseFactory.ok(storeService.update(id, request), Message.updated("Store", id));
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('store:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        storeService.delete(id);
        return ResponseFactory.deleted("Store", id);
    }
}
