package com.saranaresturantsystem.services.interfaces.inventory;

import java.math.BigDecimal;

public interface InventoryService {
    void recordTransaction(
            Long productId, Long storeId,
            BigDecimal quantity, String type,
            Long referenceId, String notes);

    void recordBankTransaction(
            Long purchaseId, Long saleId,
            Long bankId, Long expenseId, BigDecimal amount, String transactionReference, String transactionType, String description);
}
