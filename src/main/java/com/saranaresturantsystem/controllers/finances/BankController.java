package com.saranaresturantsystem.controllers.finances;

import com.saranaresturantsystem.common.Message;
import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.request.finances.BankRequest;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.dto.response.finances.BankResponse;
import com.saranaresturantsystem.services.interfaces.finances.BankService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/bank")
@Tag(name = "Bank", description = "Endpoints for managing banks")
public class BankController {
    private final BankService bankService;

    @GetMapping
    @PreAuthorize("hasAuthority('bank:read')")
    public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String, String> params) {
        return ResponseFactory.ok(bankService.getListBank(params), "Bank");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('bank:read')")
    public ResponseEntity<ApiResponse<BankResponse>> getById(@PathVariable Long id) {
        return ResponseFactory.ok(bankService.getBankResponseById(id), Message.getById("Bank", id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('bank:create')")
    public ResponseEntity<ApiResponse<BankResponse>> create(@Valid @RequestBody BankRequest request) {
        return ResponseFactory.created(bankService.createBank(request), "Bank");
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('bank:update')")
    public ResponseEntity<ApiResponse<BankResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody BankRequest request) {
        return ResponseFactory.ok(bankService.updateBank(id, request), Message.updated("Bank", id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('bank:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        bankService.deleteBank(id);
        return ResponseFactory.deleted("Bank", id);
    }
}
