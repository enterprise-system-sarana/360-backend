package com.saranaresturantsystem.services.impl.purchases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.dto.request.purchases.PurchaseItemRequest;
import com.saranaresturantsystem.dto.request.purchases.PurchaseRequest;
import com.saranaresturantsystem.dto.response.purchases.PurchaseResponse;
import com.saranaresturantsystem.entities.purchase.PurchaseItem;
import com.saranaresturantsystem.entities.purchase.Purchases;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.purchase.PurchaseMapper;
import com.saranaresturantsystem.repository.purchases.PurchaseItemsRepository;
import com.saranaresturantsystem.repository.purchases.PurchasesRepository;
import com.saranaresturantsystem.services.interfaces.catalog.ProductService;
import com.saranaresturantsystem.services.interfaces.inventory.StockService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.saranaresturantsystem.constants.Constants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchasesRepository purchasesRepository;
    private final PurchaseItemsRepository purchaseItemsRepository;
    private final StockService stockService;
    private final ObjectMapper objectMapper;
    private final PurchaseMapper purchaseMapper;
    private final ProductService productService;

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
        purchases.setStatus(Constants.COMPLETED);

        List<PurchaseItem> itemsToProcess = new ArrayList<>();
        if (request.items() != null) {
            for (PurchaseItemRequest itemReq : request.items()) {
                validateItemSerials(itemReq);
                var product = productService.findById(itemReq.productId());
                PurchaseItem item = new PurchaseItem();
                item.setPurchase(purchases);
                item.setProduct(product);

                BigDecimal qty = itemReq.quantity() != null ? itemReq.quantity() : BigDecimal.ZERO;
                BigDecimal cost = itemReq.cost() != null ? itemReq.cost() : BigDecimal.ZERO;
                BigDecimal subtotal = qty.multiply(cost);

                item.setQuantity(qty);
                item.setCost(cost);
                item.setSubtotal(subtotal);
                itemsToProcess.add(item);
            }
        }

        calculateTotalsAndPaymentStatus(purchases, itemsToProcess, request.discount(), request.paidAmount());
        Purchases savedPurchase = purchasesRepository.save(purchases);

        List<PurchaseItem> savedItems = new ArrayList<>();
        if (request.items() != null) {
            for (int i = 0; i < itemsToProcess.size(); i++) {
                PurchaseItem item = itemsToProcess.get(i);
                PurchaseItemRequest itemReq = request.items().get(i);
                item.setPurchase(savedPurchase);
                savedItems.add(purchaseItemsRepository.save(item));

                stockService.processPurchaseStock(
                        savedPurchase.getStoreId(),
                        savedPurchase.getId(),
                        savedPurchase.getReferenceNo(),
                        itemReq.productId(),
                        item.getQuantity(),
                        item.getCost(),
                        itemReq.price(),
                        itemReq.serialNumbers(),
                        item
                );
            }
        }
        savedPurchase.setPurchaseItems(savedItems);
        return purchaseMapper.toResponse(savedPurchase);
    }

    private void validateItemSerials(PurchaseItemRequest itemReq) {
        if (itemReq.serialNumbers() != null && !itemReq.serialNumbers().isEmpty()) {
            int qtyInt = itemReq.quantity() != null ? itemReq.quantity().intValue() : 0;
            int serialCount = itemReq.serialNumbers().size();
            if (serialCount != qtyInt) {
                throw new IllegalArgumentException("Serial numbers count (" + serialCount + ") does not match item quantity (" + qtyInt + ") for product ID " + itemReq.productId());
            }
        }
    }

    private void calculateTotalsAndPaymentStatus(Purchases purchase, List<PurchaseItem> items, BigDecimal discountReq, BigDecimal paidReq) {
        BigDecimal total = BigDecimal.ZERO;
        if (items != null) {
            total = items.stream()
                    .map(item -> item.getSubtotal() != null ? item.getSubtotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        BigDecimal discount = discountReq != null ? discountReq : BigDecimal.ZERO;
        BigDecimal grandTotal = total.subtract(discount).max(BigDecimal.ZERO);
        BigDecimal paid = paidReq != null ? paidReq : BigDecimal.ZERO;
        BigDecimal due = grandTotal.subtract(paid).max(BigDecimal.ZERO);

        purchase.setTotal(total);
        purchase.setDiscount(discount);
        purchase.setGrandTotal(grandTotal);
        purchase.setPaidAmount(paid);
        purchase.setDueAmount(due);
        purchase.setPaymentStatus(determinePaymentStatus(paid, grandTotal));
    }

    private String determinePaymentStatus(BigDecimal paid, BigDecimal grandTotal) {
        if (paid == null || paid.signum() == 0) {
            return PENDING;
        }
        if (paid.compareTo(grandTotal) < 0) {
            return PARTIAL;
        }
        return PAID;
    }

    @Override
    @Transactional
    public PurchaseResponse update(Long id, PurchaseRequest request) {
        Purchases purchases = findById(id);
        purchaseMapper.updateEntityFromRequest(request, purchases);
        calculateTotalsAndPaymentStatus(purchases, purchases.getPurchaseItems(), request.discount(), request.paidAmount());
        Purchases updatedPurchase = purchasesRepository.save(purchases);
        return purchaseMapper.toResponse(updatedPurchase);
    }

    @Override
    @Transactional
    public PurchaseResponse delete(Long id) {
        Purchases purchases = findById(id);
        purchases.setStatus(CANCELLED);
        Purchases cancelledPurchase = purchasesRepository.save(purchases);
        stockService.reversePurchaseStock(purchases.getStoreId(), purchases.getId(), purchases.getReferenceNo(), "SYSTEM");
        return purchaseMapper.toResponse(cancelledPurchase);
    }
}