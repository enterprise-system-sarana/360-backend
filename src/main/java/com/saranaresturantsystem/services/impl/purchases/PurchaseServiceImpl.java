package com.saranaresturantsystem.services.impl.purchases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.common.InvoiceNumberService;
import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.dto.request.purchases.PurchaseItemRequest;
import com.saranaresturantsystem.dto.request.purchases.PurchaseRequest;
import com.saranaresturantsystem.dto.response.purchases.PurchaseResponse;
import com.saranaresturantsystem.entities.catalog.Product;
import com.saranaresturantsystem.entities.sales.Payment;
import com.saranaresturantsystem.entities.purchase.PurchaseItem;
import com.saranaresturantsystem.entities.purchase.Purchase;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.purchase.PurchaseMapper;
import com.saranaresturantsystem.repository.purchases.PurchaseItemsRepository;
import com.saranaresturantsystem.repository.purchases.PurchasesRepository;
import com.saranaresturantsystem.repository.sales.PaymentRepository;
import com.saranaresturantsystem.services.interfaces.catalog.ProductService;
import com.saranaresturantsystem.services.interfaces.finances.BankService;
import com.saranaresturantsystem.services.interfaces.inventory.InventoryService;
import com.saranaresturantsystem.services.interfaces.inventory.StockService;
import com.saranaresturantsystem.services.interfaces.inventory.StoreService;
import com.saranaresturantsystem.services.interfaces.purchases.PurchaseService;
import com.saranaresturantsystem.services.interfaces.purchases.SupplierService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.saranaresturantsystem.constants.Constants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    @Override
    public Purchase savePurchase(Purchase purchase) {
        return purchasesRepository.save(purchase);
    }

    private final PurchasesRepository purchasesRepository;
    private final PaymentRepository paymentRepository;
    private final PurchaseItemsRepository purchaseItemsRepository;
    private final StockService stockService;
    private final ObjectMapper objectMapper;
    private final PurchaseMapper purchaseMapper;
    private final ProductService productService;
    private final InvoiceNumberService invoiceService ;
    private  final StoreService storeService;
    private  final InventoryService inventoryService ;
    private  final SupplierService supplierService ;
    private  final BankService bankService ;
    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseResponse> findAll(Map<String, String> params) {
        PurchaseFilter filter = objectMapper.convertValue(params, PurchaseFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Purchase> spec = PurchaseSpec.filterBy(filter);
        return purchasesRepository.findAll(spec, pageable).map(purchaseMapper::toResponse);
    }

    @Override
    public Purchase findById(Long id) {
        return purchasesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseResponse findByIdResponse(Long id) {
        Purchase purchases = findById(id);
        return purchaseMapper.toResponse(purchases);
    }

    @Override
    @Transactional
    public PurchaseResponse save(PurchaseRequest request) {
        Purchase purchases = purchaseMapper.toEntity(request);
        purchases.setReferenceNo(invoiceService.generate("PUR"));
        purchases.setStatus(Constants.COMPLETED);
//        var storeId = storeService.findById(request.storeId());
        List<PurchaseItem> itemsToProcess = new ArrayList<>();
        if (request.items() != null) {
            for (PurchaseItemRequest itemReq : request.items()) {
                var product = productService.findById(itemReq.productId());
                validateItemSerials(itemReq, product);
                PurchaseItem item = getPurchaseItem(itemReq, purchases, product);
                itemsToProcess.add(item);
            }
        }

        calculateTotalsAndPaymentStatus(purchases, itemsToProcess, request.discount(), request.paidAmount());
        var store  = storeService.findById(request.storeId());
        var supplier = supplierService.findById(request.supplierId());
        var bank = bankService.getBankById(request.bankId());
        purchases.setSuppliers(supplier);
        purchases.setBanks(bank);
        purchases.setStores(store);

        Purchase savedPurchase = purchasesRepository.save(purchases);
        savePayment(savedPurchase, request.paidAmount());

        if (request.bankId() != null && request.paidAmount() != null && request.paidAmount().signum() > 0) {
            inventoryService.recordBankTransaction(
                    savedPurchase.getId(),
                    null,
                    request.bankId(),
                    null,
                    request.paidAmount(),
                    savedPurchase.getReferenceNo() + "-PAY",
                    "PURCHASE",
                    "Bank payment for purchase " + savedPurchase.getReferenceNo()
            );
        }

        List<PurchaseItem> savedItems = new ArrayList<>();
        if (request.items() != null) {
            for (int i = 0; i < itemsToProcess.size(); i++) {
                PurchaseItem item = itemsToProcess.get(i);
                PurchaseItemRequest itemReq = request.items().get(i);
                item.setPurchase(savedPurchase);
                savedItems.add(purchaseItemsRepository.save(item));

                boolean requiresSerial = item.getProduct().getHasSerial() == null || Boolean.TRUE.equals(item.getProduct().getHasSerial());
                List<String> serialsToProcess = requiresSerial ? itemReq.serialNumbers() : null;

                stockService.processPurchaseStock(
                        savedPurchase.getStores().getId(),
                        savedPurchase.getId(),
                        savedPurchase.getReferenceNo(),
                        itemReq.productId(),
                        item.getQuantity(),
                        item.getCost(),
                        itemReq.price() != null ? itemReq.price() : item.getPrice(),
                        serialsToProcess,
                        item
                );
            }
        }
        savedPurchase.setPurchaseItems(savedItems);
        return purchaseMapper.toResponse(savedPurchase);
    }

    private void savePayment(Purchase purchase, BigDecimal paidAmount) {
        if (paidAmount == null || paidAmount.signum() <= 0) {
            return;
        }

//        Payment payment = new Payment();
//        payment.setPaymentNo(invoiceService.generate("PAY"));
//        payment.setTransactionNo("PURCHASE");
//        payment.setPurchase(purchase);
//        payment.setBanks(purchase.getBanks());
//        payment.setAmount(paidAmount);
//        payment.setPaymentDate(LocalDateTime.now());
//        payment.setStatus(PAID);
//        paymentRepository.save(payment);
    }

    private static PurchaseItem getPurchaseItem(PurchaseItemRequest itemReq, Purchase purchases, Product product) {
        PurchaseItem item = new PurchaseItem();
        item.setPurchase(purchases);
        item.setProduct(product);

        BigDecimal qty = itemReq.quantity() != null ? itemReq.quantity() : BigDecimal.ZERO;
        BigDecimal costPrice = (itemReq.cost() != null && itemReq.cost().compareTo(BigDecimal.ZERO) > 0)
                ? itemReq.cost()
                : (product.getCostPrice() != null ? product.getCostPrice() : BigDecimal.ZERO);
        BigDecimal salePrice = (itemReq.price() != null && itemReq.price().compareTo(BigDecimal.ZERO) > 0)
                ? itemReq.price()
                : (product.getSalePrice() != null ? product.getSalePrice() : BigDecimal.ZERO);
        BigDecimal subtotal = qty.multiply(costPrice);

        item.setQuantity(qty);
        item.setCost(costPrice);
        item.setPrice(salePrice);
        item.setSubtotal(subtotal);
        return item;
    }

    private void validateItemSerials(PurchaseItemRequest itemReq, Product product) {
        boolean requiresSerial = product.getHasSerial() == null || Boolean.TRUE.equals(product.getHasSerial());
        int qtyInt = itemReq.quantity() != null ? itemReq.quantity().intValue() : 0;
        int serialCount = itemReq.serialNumbers() != null ? itemReq.serialNumbers().size() : 0;

        if (requiresSerial) {
            if (serialCount != qtyInt) {
                throw new IllegalArgumentException(
                        "Product '" + product.getName() + "' requires " + qtyInt + " serial numbers, but " + serialCount + " were provided");
            }
        }
    }

    private void calculateTotalsAndPaymentStatus(Purchase purchase, List<PurchaseItem> items, BigDecimal discountReq, BigDecimal paidReq) {
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
        Purchase purchases = findById(id);
        purchaseMapper.updateEntityFromRequest(request, purchases);
        calculateTotalsAndPaymentStatus(purchases, purchases.getPurchaseItems(), request.discount(), request.paidAmount());
        Purchase updatedPurchase = purchasesRepository.save(purchases);
        return purchaseMapper.toResponse(updatedPurchase);
    }

    @Override
    @Transactional
    public PurchaseResponse delete(Long id) {
        Purchase purchases = findById(id);
        purchases.setStatus(Constants.CANCELLED);
        Purchase cancelledPurchase = purchasesRepository.save(purchases);
        stockService.reversePurchaseStock(purchases.getStores().getId(), purchases.getId(), purchases.getReferenceNo(), "SYSTEM");
        return purchaseMapper.toResponse(cancelledPurchase);
    }
}