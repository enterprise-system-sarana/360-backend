package com.saranaresturantsystem.dto.request.purchases;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PurchaseRequest(
        @NotBlank(message = "Reference number is required")
        String referenceNo,

        @NotNull(message = "Supplier ID is required")
        Long supplierId,

        @NotNull(message = "Store ID is required")
        Long storeId,

        @NotNull(message = "Purchase date is required")
        LocalDate purchaseDate,

        @PositiveOrZero(message = "Total must be zero or positive")
        BigDecimal total,

        @PositiveOrZero(message = "Discount must be zero or positive")
        BigDecimal discount,

        @PositiveOrZero(message = "Grand total must be zero or positive")
        BigDecimal grandTotal,

        @PositiveOrZero(message = "Paid amount must be zero or positive")
        BigDecimal paidAmount,

        String paymentStatus,

        String status,

        String note,

        @NotEmpty(message = "Purchase items cannot be empty")
        @Valid
        List<PurchaseItemRequest> items
) {

    public PurchaseRequest {
        discount = (discount == null) ? BigDecimal.ZERO : discount;
        paidAmount = (paidAmount == null) ? BigDecimal.ZERO : paidAmount;
        items = (items == null) ? List.of() : List.copyOf(items);
    }
}