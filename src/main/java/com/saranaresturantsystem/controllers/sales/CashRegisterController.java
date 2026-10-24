package com.saranaresturantsystem.controllers.sales;

import com.saranaresturantsystem.common.Message;
import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.request.sales.CloseCashRegisterRequest;
import com.saranaresturantsystem.dto.request.sales.OpenCashRegisterRequest;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.dto.response.sales.CashRegisterResponse;
import com.saranaresturantsystem.services.interfaces.sales.CashRegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cash-registers")
@Tag(name = "Cash Register", description = "Endpoints for managing daily cashier shifts (open/close sale)")
public class CashRegisterController {

    private final CashRegisterService cashRegisterService;

    @GetMapping
    @PreAuthorize("hasAuthority('sale:read')")
    @Operation(summary = "Get list of cash registers with pagination and filters")
    public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String, String> params) {
        Page<CashRegisterResponse> page = cashRegisterService.getAll(params);
        return ResponseFactory.ok(page, "Cash Register");
    }

    @PostMapping("/open")
    @PreAuthorize("hasAuthority('sale:create')")
    @Operation(summary = "Open a new cash register shift (Open Sale)")
    public ResponseEntity<ApiResponse<CashRegisterResponse>> openRegister(
            @Valid @RequestBody OpenCashRegisterRequest request,
            Principal principal) {
        String createdBy = principal != null ? principal.getName() : "system";
        return ResponseFactory.created(cashRegisterService.openRegister(request, createdBy), "Cash Register");
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasAuthority('sale:update')")
    @Operation(summary = "Close a cash register shift (Close Sale)")
    public ResponseEntity<ApiResponse<CashRegisterResponse>> closeRegister(
            @PathVariable Long id,
            @Valid @RequestBody CloseCashRegisterRequest request,
            Principal principal) {
        String updatedBy = principal != null ? principal.getName() : "system";
        return ResponseFactory.ok(cashRegisterService.closeRegister(id, request, updatedBy), "Cash register closed successfully");
    }

    @GetMapping("/current")
    @PreAuthorize("hasAuthority('sale:read')")
    @Operation(summary = "Get the current user's open cash register")
    public ResponseEntity<ApiResponse<CashRegisterResponse>> getCurrentOpen() {
        return ResponseFactory.ok(cashRegisterService.getCurrentOpen(), "Current open cash register retrieved");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('sale:read')")
    @Operation(summary = "Get cash register details by ID")
    public ResponseEntity<ApiResponse<CashRegisterResponse>> getById(@PathVariable Long id) {
        return ResponseFactory.ok(cashRegisterService.getById(id), Message.getById("Cash Register", id));
    }

//    @GetMapping
//    @PreAuthorize("hasAuthority('sale:read')")
//    @Operation(summary = "Get list of cash registers with pagination and filters")
//    public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String, String> params) {
//        Page<CashRegisterResponse> page = cashRegisterService.getAll(params);
//        return ResponseFactory.ok(page, "Cash Register");
//    }
}
