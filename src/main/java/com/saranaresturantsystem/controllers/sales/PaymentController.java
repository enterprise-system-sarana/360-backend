package com.saranaresturantsystem.controllers.sales;

import com.saranaresturantsystem.common.Message;
import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.PageDTO;
import com.saranaresturantsystem.dto.request.sales.PaymentRequest;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.dto.response.sales.PaymentResponse;
import com.saranaresturantsystem.services.interfaces.sales.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment", description = "Endpoints for managing customer payments and tracking sale balances")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Create payment against a sale")
    public ResponseEntity<ApiResponse<PaymentResponse>> create(@Valid @RequestBody PaymentRequest request) {
        return ResponseFactory.created(paymentService.create(request), "Payment");
    }

    @GetMapping
    @Operation(summary = "Get list of payments with pagination and filters")
    public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String, String> params) {
        Page<PaymentResponse> page = paymentService.findAll(params);
        return ResponseFactory.ok(page, "Payment");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment details by ID")
    public ResponseEntity<ApiResponse<PaymentResponse>> getById(@PathVariable Long id) {
        return ResponseFactory.ok(paymentService.getById(id), Message.getById("Payment", id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payment record")
    public ResponseEntity<ApiResponse<PaymentResponse>> update(@PathVariable Long id,
                                                               @Valid @RequestBody PaymentRequest request) {
        return ResponseFactory.ok(paymentService.update(request, id), Message.updated("Payment", id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a payment record")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        paymentService.delete(id);
        return ResponseFactory.deleted("Payment", id);
    }
}
