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
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/expense-types")
@Tag(name = "Expense Type", description = "Endpoints for managing expense types")
public class ExpenseTypeController {

    private final ExpenseTypeService expenseTypeService;
    private final ExpenseTypeMapper expenseTypeMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String, String> params) {
        return ResponseFactory.ok(expenseTypeService.findAll(params), "Expense Type");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseTypeResponse>> getById(@PathVariable Long id) {
        ExpenseType expenseType = expenseTypeService.findById(id);
        ExpenseTypeResponse response = expenseTypeMapper.toResponse(expenseType);
        return ResponseFactory.ok(response, Message.getById("Expense Type", id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseTypeResponse>> create(@Valid @RequestBody ExpenseTypeRequest request) {
        return ResponseFactory.created(expenseTypeService.save(request), "Expense Type");
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<ExpenseTypeResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseTypeRequest request) {
        return ResponseFactory.ok(expenseTypeService.update(id, request), Message.updated("Expense Type", id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        expenseTypeService.delete(id);
        return ResponseFactory.deleted("Expense Type", id);
    }
}