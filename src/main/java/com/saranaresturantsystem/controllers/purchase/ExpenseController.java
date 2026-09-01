package com.saranaresturantsystem.controllers.purchase;

import com.saranaresturantsystem.common.Message;
import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.request.purchases.ExpenseRequest;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.dto.response.purchases.ExpenseResponse;
import com.saranaresturantsystem.entities.purchase.Expenses;
import com.saranaresturantsystem.mappers.purchase.ExpenseMapper;
import com.saranaresturantsystem.services.interfaces.purchases.ExpenseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/expenses")
@Tag(name = "Expense", description = "Endpoints for managing expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ExpenseMapper expenseMapper;

    @GetMapping
    @PreAuthorize("hasAuthority('expense:read')")
    public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String, String> params) {
        return ResponseFactory.ok(expenseService.findAll(params), "Expense");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('expense:read')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getById(@PathVariable Long id) {
        Expenses expense = expenseService.findById(id);
        ExpenseResponse response = expenseMapper.toResponse(expense);
        return ResponseFactory.ok(response, Message.getById("Expense", id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('expense:create')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> create(@Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse response = expenseService.save(request);
        return ResponseFactory.created(response, "Expense");
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('expense:update')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse response = expenseService.update(id, request);
        return ResponseFactory.ok(response, Message.updated("Expense", id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('expense:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        expenseService.delete(id);
        return ResponseFactory.deleted("Expense", id);
    }
}