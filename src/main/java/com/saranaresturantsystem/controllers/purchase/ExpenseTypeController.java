package com.saranaresturantsystem.controllers.purchase;

import com.saranaresturantsystem.common.Message;
import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.request.purchases.ExpenseTypeRequest;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.dto.response.purchases.ExpenseTypeResponse;
import com.saranaresturantsystem.entities.purchase.ExpenseType;
import com.saranaresturantsystem.mappers.purchase.ExpenseTypeMapper;
import com.saranaresturantsystem.services.interfaces.purchases.ExpenseTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/expense-types")
@Tag(name = "Expense Type", description = "Endpoints for managing expense types")
public class ExpenseTypeController {

    private final ExpenseTypeService expenseTypeService;

    @GetMapping
    @PreAuthorize("hasAuthority('expensesType:read')")
    public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String, String> params) {
        return ResponseFactory.ok(expenseTypeService.findAll(params), "Expense Type");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('expensesType:read')")
    public ResponseEntity<ApiResponse<ExpenseTypeResponse>> getById(@PathVariable Long id) {

        return ResponseFactory.ok(expenseTypeService.getById(id), Message.getById("Expense Type", id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('expensesType:create')")
    public ResponseEntity<ApiResponse<ExpenseTypeResponse>> create(@Valid @RequestBody ExpenseTypeRequest request) {
        return ResponseFactory.created(expenseTypeService.save(request), "Expense Type");
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('expensesType:update')")
    public ResponseEntity<ApiResponse<ExpenseTypeResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseTypeRequest request) {
        return ResponseFactory.ok(expenseTypeService.update(id, request), Message.updated("Expense Type", id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('expensesType:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        expenseTypeService.delete(id);
        return ResponseFactory.deleted("Expense Type", id);
    }
}