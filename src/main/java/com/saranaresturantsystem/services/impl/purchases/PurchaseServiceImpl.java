package com.saranaresturantsystem.services.impl.purchases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.dto.request.purchases.PurchaseItemRequest;
import com.saranaresturantsystem.dto.request.purchases.PurchaseRequest;
import com.saranaresturantsystem.dto.response.purchases.PurchaseResponse;
import com.saranaresturantsystem.entities.catalog.ProductSerials;
import com.saranaresturantsystem.entities.inventory.Inventory_Transactions;
import com.saranaresturantsystem.entities.purchase.Purchase_Items;
import com.saranaresturantsystem.entities.purchase.Purchases;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.purchase.PurchaseMapper;
import com.saranaresturantsystem.repository.Inventory.InventoryTransactionsRepository;
import com.saranaresturantsystem.repository.catalog.ProductSerialsRepository;
import com.saranaresturantsystem.repository.purchases.PurchaseItemsRepository;
import com.saranaresturantsystem.repository.purchases.PurchasesRepository;
import com.saranaresturantsystem.services.interfaces.purchases.PurchaseService;
import com.saranaresturantsystem.specification.purchases.purchases.PurchaseFilter;
import com.saranaresturantsystem.specification.purchases.purchases.PurchaseSpec;
import com.saranaresturantsystem.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchasesRepository purchasesRepository;
    private final PurchaseItemsRepository purchaseItemsRepository;
    private final ProductSerialsRepository productSerialsRepository;
    private final InventoryTransactionsRepository inventoryTransactionsRepository;
    private final ObjectMapper objectMapper;
    private final PurchaseMapper purchaseMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseResponse> findAll(Map<String, String> params) {
        PurchaseFilter filter = objectMapper.convertValue(params, PurchaseFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Purchases> spec = PurchaseSpec.filterBy(filter);
        return purchasesRepository.findAll(spec, pageable).map(purchaseMapper::toResponse);
    }

    @Override
    public Purchases findById(Long id) {
        return purchasesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseResponse findByIdResponse(Long id) {
        Purchases purchases = findById(id);
        return purchaseMapper.toResponse(purchases);
    }

    @Override
    @Transactional
    public PurchaseResponse save(PurchaseRequest request) {
        Purchases purchases = purchaseMapper.toEntity(request);
        if (purchases.getStatus() == null || purchases.getStatus().isEmpty()) {
            purchases.setStatus("COMPLETED");
        }
        Purchases savedPurchase = purchasesRepository.save(purchases);

        if (request.getItems() != null) {
            for (PurchaseItemRequest itemReq : request.getItems()) {
                Purchase_Items item = new Purchase_Items();
                item.setPurchase(savedPurchase);
                item.setProductId(itemReq.getProductId());
                item.setQuantity(itemReq.getQuantity());
                item.setCost(itemReq.getCost());
                item.setSubtotal(itemReq.getSubtotal());

                purchaseItemsRepository.save(item);

                if (itemReq.getSerialNumbers() != null && !itemReq.getSerialNumbers().isEmpty()) {
                    for (String barcode : itemReq.getSerialNumbers()) {
                        ProductSerials serial = new ProductSerials();
                        serial.setProductId(itemReq.getProductId());
                        serial.setBarcode(barcode);
                        serial.setCost(itemReq.getCost());
                        serial.setPrice(itemReq.getCost() != null ? itemReq.getCost().multiply(new BigDecimal("1.2")) : BigDecimal.ZERO);
                        serial.setQuantity(BigDecimal.ONE);
                        serial.setStoreId(savedPurchase.getStoreId());
                        serial.setPurchaseId(savedPurchase.getId());
                        serial.setStatus("AVAILABLE");
                        serial.setDeleted(0);

                        productSerialsRepository.save(serial);
                    }
                }

                Inventory_Transactions tx = new Inventory_Transactions();
                tx.setProductId(itemReq.getProductId());
                tx.setStoreId(savedPurchase.getStoreId() != null ? savedPurchase.getStoreId() : 1L);
                tx.setQuantity(itemReq.getQuantity());
                tx.setType("PURCHASE");
                tx.setReferenceId(savedPurchase.getId());
                tx.setTransactionDate(LocalDateTime.now());
                tx.setNotes("Purchase Ref: " + savedPurchase.getReferenceNo());

                inventoryTransactionsRepository.save(tx);
            }
        }

        return purchaseMapper.toResponse(savedPurchase);
    }

    @Override
    @Transactional
    public PurchaseResponse update(Long id, PurchaseRequest request) {
        Purchases purchases = findById(id);
        purchaseMapper.updateEntityFromRequest(request, purchases);
        Purchases updatedPurchase = purchasesRepository.save(purchases);
        return purchaseMapper.toResponse(updatedPurchase);
    }

    @Override
    @Transactional
    public PurchaseResponse delete(Long id) {
        Purchases purchases = findById(id);
        purchases.setStatus("CANCELLED");
        Purchases cancelledPurchase = purchasesRepository.save(purchases);
        return purchaseMapper.toResponse(cancelledPurchase);
    }
}