package com.saranaresturantsystem.services.impl.inventory;

import com.saranaresturantsystem.entities.inventory.InventoryTransaction;
import com.saranaresturantsystem.repository.Inventory.InventoryTransactionsRepository;
import com.saranaresturantsystem.services.interfaces.inventory.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryTransactionsRepository inventoryTransactionsRepository;

    @Override
    @Transactional
    public void recordTransaction(Long productId, Long storeId, BigDecimal quantity,
                                  String type, Long referenceId, String notes) {
        InventoryTransaction tx = new InventoryTransaction();
        tx.setProductId(productId);
        tx.setStoreId(storeId != null ? storeId : 1L);
        tx.setQuantity(quantity);
        tx.setType(type);
        tx.setReferenceId(referenceId);
        tx.setTransactionDate(LocalDateTime.now());
        tx.setNotes(notes);
        inventoryTransactionsRepository.save(tx);
        log.debug("Transaction recorded: type={} product={} store={} qty={}", type, productId, storeId, quantity);
    }
}
