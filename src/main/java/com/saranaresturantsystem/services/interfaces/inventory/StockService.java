package com.saranaresturantsystem.services.interfaces.inventory;

import com.saranaresturantsystem.dto.response.inventory.StockResponse;
import com.saranaresturantsystem.entities.purchase.PurchaseItem;
import com.saranaresturantsystem.entities.sales.SaleItems;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface StockService {
    Page<StockResponse> findAll(Map<String , String> params);
    // this is for logic purchase and increment stock
    void processPurchaseStock(Long storeId, Long purchaseId, String referenceNo, Long productId, BigDecimal quantity, BigDecimal cost , BigDecimal price , List<String> serialNumbers, PurchaseItem purchaseItem);

    void reversePurchaseStock(Long storeId, Long purchaseId, String referenceNo, String updatedBy);

    void deductSaleStock(Long storeId, Long saleId, String saleNo, Long productId, BigDecimal quantity, List<Long> serialIds, String updatedBy);

    void restoreSaleStock(Long storeId, Long saleId, String saleNo, List<SaleItems> items, String updatedBy);

    @Transactional
    void adjustStock(Long productId, Long storeId, BigDecimal delta);
}
