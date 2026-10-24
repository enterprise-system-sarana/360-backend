package com.saranaresturantsystem.services.impl.sale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.common.InvoiceNumberService;
import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.dto.request.sales.SaleItemRequest;
import com.saranaresturantsystem.dto.request.sales.SaleRequest;
import com.saranaresturantsystem.dto.request.sales.SaleReturnItemRequest;
import com.saranaresturantsystem.dto.request.sales.SaleReturnRequest;
import com.saranaresturantsystem.dto.response.sales.SaleResponse;
import com.saranaresturantsystem.entities.sales.SaleItems;
import com.saranaresturantsystem.entities.sales.Sales;
import com.saranaresturantsystem.entities.users.User;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.sale.SaleMapper;
import com.saranaresturantsystem.repository.catalog.ProductSerialsRepository;
import com.saranaresturantsystem.repository.sales.SaleRepository;
import com.saranaresturantsystem.repository.sales.CashRegisterRepository;
import com.saranaresturantsystem.repository.sales.PaymentRepository;
import com.saranaresturantsystem.services.interfaces.catalog.ProductService;
import com.saranaresturantsystem.services.interfaces.customer.CustomerService;
import com.saranaresturantsystem.services.interfaces.finances.BankService;
import com.saranaresturantsystem.services.interfaces.inventory.InventoryService;
import com.saranaresturantsystem.services.interfaces.inventory.StockService;
import com.saranaresturantsystem.services.interfaces.inventory.StoreService;
import com.saranaresturantsystem.services.interfaces.sales.SaleService;
import com.saranaresturantsystem.services.interfaces.users.UserService;
import com.saranaresturantsystem.specification.sales.SaleFilter;
import com.saranaresturantsystem.specification.sales.SaleSpec;
import com.saranaresturantsystem.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.saranaresturantsystem.constants.Constants.*;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final CashRegisterRepository cashRegisterRepository;
    private final PaymentRepository paymentRepository;
    private final ProductSerialsRepository productSerialsRepository;
    private final SaleMapper saleMapper;
    private final ObjectMapper objectMapper;
    private final ProductService productService;
    private final StockService stockService;
    private final InvoiceNumberService invoiceNumberService;
    private final StoreService storeService;
    private final InventoryService inventoryService;
    private final BankService bankService;
    private final CustomerService customerService;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public Page<SaleResponse> getAll(Map<String, String> params) {
        SaleFilter filter = objectMapper.convertValue(params, SaleFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Sales> spec = SaleSpec.filter(filter);
        return saleRepository.findAll(spec, pageable).map(saleMapper::toResponse);
    }

    @Override
    @Transactional
    public SaleResponse create(SaleRequest request, String createdBy) {
        var bank = request.bankId() == null ? null : bankService.getBankById(request.bankId());
        var storeId = storeService.findById(request.storeId());
        var customerId = customerService.findById(request.customerId());
        Sales sale = saleMapper.toEntity(request);
        sale.setStore(storeId);
        sale.setBanks(bank);
        sale.setCustomer(customerId);
        User user = currentUser(createdBy);
        sale.setUser(user);
        sale.setNo(invoiceNumberService.generate("POS"));
        sale.setDate(LocalDateTime.now());
        sale.setSaleStatus(COMPLETED);
        sale.setCreatedBy(createdBy);

        replaceItems(sale, request.items());


//        BigDecimal totalPaid = totalPaidFromPayments.compareTo(BigDecimal.ZERO) > 0
//                ? totalPaidFromPayments
//                : (request.paidAmount() != null ? BigDecimal.valueOf(request.paidAmount()) : BigDecimal.ZERO);

//        sale.setPaidAmount(totalPaid.doubleValue());

        // If bank wasn't set globally on sale, find first bank used in payments

        BigDecimal cashPaid = BigDecimal.ZERO;
        BigDecimal bankPaid = BigDecimal.ZERO;

        if (request.cashAmount() != null && request.cashAmount() > 0) {
            cashPaid = cashPaid.add(BigDecimal.valueOf(request.cashAmount()));
        }
        if (request.bankAmount() != null && request.bankAmount() > 0) {
            bankPaid = bankPaid.add(BigDecimal.valueOf(request.bankAmount()));
        }
        if (cashPaid.signum() == 0 && bankPaid.signum() == 0 && request.paidAmount() != null && request.paidAmount() > 0) {
            if ("BANK".equalsIgnoreCase(request.paymentMethod()) || "ABA".equalsIgnoreCase(request.paymentMethod())) {
                bankPaid = BigDecimal.valueOf(request.paidAmount());
            } else {
                cashPaid = BigDecimal.valueOf(request.paidAmount());
            }
        }
        sale.setPaidAmount(cashPaid.add(bankPaid).doubleValue());

        calculateTotalsAndPaymentStatus(sale);

        if (user != null) {
            final BigDecimal finalCashPaid = cashPaid;
            final BigDecimal finalBankPaid = bankPaid;
            cashRegisterRepository.findOpenRegisterByUserId(user.getId()).ifPresent(register -> {
                sale.setCashRegister(register);
                BigDecimal currentSales = register.getTotalSales() != null ? register.getTotalSales() : BigDecimal.ZERO;
                BigDecimal saleTotal = sale.getGrandTotal() != null ? BigDecimal.valueOf(sale.getGrandTotal()) : BigDecimal.ZERO;
                register.setTotalSales(currentSales.add(saleTotal));

                BigDecimal change = sale.getReturnAmount() != null ? BigDecimal.valueOf(sale.getReturnAmount()) : BigDecimal.ZERO;
                BigDecimal netCash = finalCashPaid.subtract(change).max(BigDecimal.ZERO);

                BigDecimal currentCash = register.getTotalCashSales() != null ? register.getTotalCashSales() : BigDecimal.ZERO;
                BigDecimal currentBank = register.getTotalBankSales() != null ? register.getTotalBankSales() : BigDecimal.ZERO;
                register.setTotalCashSales(currentCash.add(netCash));
                register.setTotalBankSales(currentBank.add(finalBankPaid));

                int currentCount = register.getSaleCount() != null ? register.getSaleCount() : 0;
                register.setSaleCount(currentCount + 1);
                cashRegisterRepository.save(register);
            });
        }

        Sales savedSale = saleRepository.save(sale);

        // Deduct stock and mark serials as SOLD for each item
        for (SaleItems item : savedSale.getItems()) {
            stockService.deductSaleStock(
                    savedSale.getStore().getId(),
                    savedSale.getId(),
                    String.valueOf(savedSale.getNo()),
                    item.getProduct().getId(),
                    item.getQuantity(),
                    item.getProductSerialIds(),
                    createdBy
            );
        }

//        List<Payment> savedPayments = savePayments(savedSale, paymentRequests, createdBy);
//        savedSale.setPaymentList(savedPayments);
        return saleMapper.toResponse(savedSale);
    }

//    private List<SalePaymentRequest> resolvePayments(SaleRequest request) {
//        List<SalePaymentRequest> payments = new ArrayList<>();
//        if (request.payments() != null && !request.payments().isEmpty()) {
//            for (SalePaymentRequest p : request.payments()) {
//                if (p != null && p.amount() != null && p.amount().compareTo(BigDecimal.ZERO) > 0) {
//                    String method = p.paymentMethod() != null && !p.paymentMethod().isBlank()
//                            ? p.paymentMethod().trim().toUpperCase()
//                            : "CASH";
//                    Long bId = "BANK".equalsIgnoreCase(method)
//                            ? (p.bankId() != null ? p.bankId() : request.bankId())
//                            : null;
//                    payments.add(new SalePaymentRequest(method, bId, p.amount(), p.transactionNo(), p.note()));
//                }
//            }
//        } else if (request.cashAmount() != null || request.bankAmount() != null) {
//            if (request.cashAmount() != null && request.cashAmount() > 0) {
//                payments.add(new SalePaymentRequest("CASH", null, BigDecimal.valueOf(request.cashAmount()), null, null));
//            }
//            if (request.bankAmount() != null && request.bankAmount() > 0) {
//                payments.add(new SalePaymentRequest("BANK", request.bankId(), BigDecimal.valueOf(request.bankAmount()), null, null));
//            }
//        } else if (request.paidAmount() != null && request.paidAmount() > 0) {
//            String method = request.paymentMethod() != null && !request.paymentMethod().isBlank()
//                    ? request.paymentMethod().trim().toUpperCase()
//                    : "CASH";
//            Long bId = "BANK".equalsIgnoreCase(method) ? request.bankId() : null;
//            payments.add(new SalePaymentRequest(method, bId, BigDecimal.valueOf(request.paidAmount()), null, null));
//        }
//        return payments;
//    }

//    private List<Payment> savePayments(Sales sale, List<SalePaymentRequest> payments, String createdBy) {
//        List<Payment> savedList = new ArrayList<>();
//        for (SalePaymentRequest paymentReq : payments) {
//            if (paymentReq.amount() == null || paymentReq.amount().compareTo(BigDecimal.ZERO) <= 0) {
//                continue;
//            }
//
//            Payment payment = new Payment();
//            payment.setPaymentNo(invoiceNumberService.generate("PAY"));
//            payment.setTransactionNo(paymentReq.transactionNo() != null && !paymentReq.transactionNo().isBlank()
//                    ? paymentReq.transactionNo()
//                    : "SALE");
//            payment.setSales(sale);
//            payment.setPaymentMethod(paymentReq.paymentMethod());
//            payment.setAmount(paymentReq.amount());
//            payment.setPaymentDate(LocalDateTime.now());
//            payment.setStatus(PAID);
//            payment.setCreatedBy(createdBy);
//            payment.setUser(sale.getUser());
//
//            Banks bank = null;
//            if ("BANK".equalsIgnoreCase(paymentReq.paymentMethod()) || paymentReq.bankId() != null) {
//                Long bId = paymentReq.bankId() != null ? paymentReq.bankId() : (sale.getBanks() != null ? sale.getBanks().getId() : null);
//                if (bId != null) {
//                    bank = bankService.getBankById(bId);
//                    payment.setBanks(bank);
//                }
//            }
//
//            Payment saved = paymentRepository.save(payment);
//            savedList.add(saved);
//
//            if (bank != null) {
//                inventoryService.recordBankTransaction(
//                        null,
//                        sale.getId(),
//                        bank.getId(),
//                        null,
//                        paymentReq.amount(),
//                        payment.getPaymentNo(),
//                        SALE,
//                        "Bank payment for sale " + sale.getNo()
//                );
//            }
//        }
//        return savedList;
//    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponse getById(Long id) {
        return saleMapper.toResponse(findById(id));
    }

    @Override
    @Transactional
    public SaleResponse update(Long id, SaleRequest request, String updatedBy) {
        Sales sale = findById(id);
        saleMapper.updateFromRequest(request, sale);
        if (sale.getUser() == null) {
            sale.setUser(currentUser(updatedBy));
        }
        sale.setUpdatedBy(updatedBy);
        replaceItems(sale, request.items());

//        List<SalePaymentRequest> paymentRequests = resolvePayments(request);
//        if (!paymentRequests.isEmpty()) {
//            BigDecimal totalPaid = paymentRequests.stream()
//                    .map(SalePaymentRequest::amount)
//                    .filter(Objects::nonNull)
//                    .reduce(BigDecimal.ZERO, BigDecimal::add);
//            sale.setPaidAmount(totalPaid.doubleValue());
//            //            List<Payment> savedPayments = savePayments(sale, paymentRequests, updatedBy);
//            if (sale.getPaymentList() == null) {
//                sale.setPaymentList(new ArrayList<>());
//            }
////            sale.getPaymentList().addAll(savedPayments);
//        }

        calculateTotalsAndPaymentStatus(sale);
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    @Transactional
    public SaleResponse complete(Long id, String updatedBy) {
        Sales sale = findById(id);
//        if (!sale.getSaleStatus().equals(Constants.PENDING)) {
//            throw new IllegalArgumentException("Only PENDING sales can be completed");
//        }

        for (SaleItems item : sale.getItems()) {
            var productId = productService.findById(item.getProduct().getId());

            stockService.deductSaleStock(
                    sale.getStore().getId(),
                    sale.getId(),
                    String.valueOf(sale.getNo()),
                    productId.getId(),
                    item.getQuantity(),
                    item.getProductSerialIds(),
                    updatedBy
            );
        }

        // Only record fallback bank transaction if bank is set and no payments have recorded bank transactions yet
//        List<Payment> activePayments = paymentRepository.findActiveBySalesId(sale.getId());
//        boolean hasBankPayment = activePayments.stream()
//                .anyMatch(p -> "BANK".equalsIgnoreCase(p.getPaymentMethod()) && p.getBanks() != null);

//        if (!hasBankPayment && sale.getBanks() != null) {
//            for (int i = 0; i < sale.getItems().size(); i++) {
//                SaleItems item = sale.getItems().get(i);
//                inventoryService.recordBankTransaction(
//                        null,
//                        sale.getId(),
//                        sale.getBanks().getId(),
//                        null,
//                        item.getSubTotal(),
//                        sale.getNo() + "-" + (i + 1),
//                        SALE,
//                        "Sale of product ID " + item.getProduct().getId() + " with quantity " + item.getQuantity()
//                );
//            }
//        }

        sale.setSaleStatus(COMPLETED);
        sale.setUpdatedBy(updatedBy);
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    @Transactional
    public SaleResponse closeSale(Long id, String updatedBy) {
        Sales sale = saleRepository.findByIdWithLock(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", id));

        if (Constants.CANCELLED.equalsIgnoreCase(sale.getSaleStatus()) || Constants.RETURNED.equalsIgnoreCase(sale.getSaleStatus())) {
            throw new IllegalStateException("Cannot close a cancelled or returned sale");
        }

        // Deduct inventory stock only if the sale was PENDING
        if (Constants.PENDING.equalsIgnoreCase(sale.getSaleStatus())) {
            for (SaleItems item : sale.getItems()) {
                var productId = productService.findById(item.getProduct().getId());
                stockService.deductSaleStock(
                        sale.getStore().getId(),
                        sale.getId(),
                        String.valueOf(sale.getNo()),
                        productId.getId(),
                        item.getQuantity(),
                        item.getProductSerialIds(),
                        updatedBy
                );
            }

            // Only record fallback bank transaction if bank is set and no payments have recorded bank transactions yet
            // This is to ensure that we don't record duplicate bank transactions
//            List<Payment> activePayments = paymentRepository.findActiveBySalesId(sale.getId());
//            boolean hasBankPayment = activePayments.stream()
//                    .anyMatch(p -> ("BANK".equalsIgnoreCase(p.getPaymentMethod()) || "ABA".equalsIgnoreCase(p.getPaymentMethod()) || "BANK_TRANSFER".equalsIgnoreCase(p.getPaymentMethod())) && p.getBanks() != null);

//            if (!hasBankPayment && sale.getBanks() != null) {
//                for (int i = 0; i < sale.getItems().size(); i++) {
//                    SaleItems item = sale.getItems().get(i);
//                    inventoryService.recordBankTransaction(
//                            null,
//                            sale.getId(),
//                            sale.getBanks().getId(),
//                            null,
//                            item.getSubTotal(),
//                            sale.getNo() + "-" + (i + 1),
//                            Constants.SALE,
//                            "Sale of product ID " + item.getProduct().getId() + " with quantity " + item.getQuantity()
//                    );
//                }
//            }
        }

        // Authoritatively recalculate payment totals and payment status
        BigDecimal grandTotal = sale.getGrandTotal() != null ? BigDecimal.valueOf(sale.getGrandTotal()) : BigDecimal.ZERO;
//        BigDecimal completedPaid = paymentRepository.sumCompletedPaymentsBySaleId(sale.getId());
//        sale.setPaidAmount(completedPaid.doubleValue());
//        sale.setReturnAmount(completedPaid.subtract(grandTotal).max(BigDecimal.ZERO).doubleValue());
//
//        if (completedPaid.compareTo(BigDecimal.ZERO) == 0) {
//            sale.setPaymentStatus(Constants.PENDING);
//        } else if (completedPaid.compareTo(grandTotal) < 0) {
//            sale.setPaymentStatus(Constants.PARTIAL);
//        } else {
//            sale.setPaymentStatus(Constants.PAID);
//        }

        sale.setSaleStatus(Constants.COMPLETED);
        sale.setUpdatedBy(updatedBy);
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    @Transactional
    public SaleResponse cancel(Long id, String updatedBy) {
        Sales sale = findById(id);
        if (CANCELLED.equalsIgnoreCase(sale.getSaleStatus())) {
            return saleMapper.toResponse(sale);
        }
        if (COMPLETED.equalsIgnoreCase(sale.getSaleStatus()) || PARTIAL_RETURNED.equalsIgnoreCase(sale.getSaleStatus())) {
            stockService.restoreSaleStock(
                    sale.getStore().getId(),
                    sale.getId(),
                    String.valueOf(sale.getNo()),
                    sale.getItems(),
                    updatedBy
            );
        }
        sale.setSaleStatus(CANCELLED);
        sale.setUpdatedBy(updatedBy);
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    @Transactional
    public SaleResponse returnSale(Long id, SaleReturnRequest request, String updatedBy) {
        Sales sale = findById(id);
        if (!COMPLETED.equals(sale.getSaleStatus()) && !PARTIAL_RETURNED.equals(sale.getSaleStatus())) {
            throw new IllegalStateException("Only completed or partially returned sales can be returned");
        }

        for (SaleReturnItemRequest requestItem : request.items()) {
            SaleItems saleItem = sale.getItems().stream()
                    .filter(item -> item.getId().equals(requestItem.saleItemId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Sale item", requestItem.saleItemId()));
            BigDecimal returned = saleItem.getReturnedQuantity() == null
                    ? BigDecimal.ZERO
                    : saleItem.getReturnedQuantity();
            BigDecimal remaining = saleItem.getQuantity().subtract(returned);
            if (requestItem.quantity().compareTo(remaining) > 0) {
                throw new IllegalArgumentException("Return quantity exceeds remaining quantity for sale item "
                        + requestItem.saleItemId());
            }
            if (saleItem.getProductSerialIds() != null && !saleItem.getProductSerialIds().isEmpty()
                    && (requestItem.serialNumberIds() == null
                    || requestItem.serialNumberIds().size() != requestItem.quantity().intValue()
                    || requestItem.serialNumberIds().size() != new HashSet<>(requestItem.serialNumberIds()).size()
                    || (saleItem.getReturnedProductSerialIds() != null
                    && !java.util.Collections.disjoint(
                    saleItem.getReturnedProductSerialIds(), requestItem.serialNumberIds()))
                    || !saleItem.getProductSerialIds().containsAll(requestItem.serialNumberIds()))) {
                throw new IllegalArgumentException("Serial number count must match return quantity for sale item "
                        + requestItem.saleItemId());
            }
            if ((saleItem.getProductSerialIds() == null || saleItem.getProductSerialIds().isEmpty())
                    && requestItem.serialNumberIds() != null && !requestItem.serialNumberIds().isEmpty()) {
                throw new IllegalArgumentException("Serial numbers are not valid for non-serialized sale item "
                        + requestItem.saleItemId());
            }

            SaleItems returnedItem = new SaleItems();
            returnedItem.setProduct(saleItem.getProduct());
            returnedItem.setQuantity(requestItem.quantity());
            returnedItem.setProductSerialIds(requestItem.serialNumberIds());
            stockService.restoreSaleStock(
                    sale.getStore().getId(),
                    sale.getId(),
                    String.valueOf(sale.getNo()),
                    List.of(returnedItem),
                    updatedBy);
            saleItem.setReturnedQuantity(returned.add(requestItem.quantity()));
            if (requestItem.serialNumberIds() != null && !requestItem.serialNumberIds().isEmpty()) {
                if (saleItem.getReturnedProductSerialIds() == null) {
                    saleItem.setReturnedProductSerialIds(new ArrayList<>());
                }
                saleItem.getReturnedProductSerialIds().addAll(requestItem.serialNumberIds());
            }
        }

        boolean fullyReturned = sale.getItems().stream()
                .allMatch(item -> {
                    BigDecimal returned = item.getReturnedQuantity() == null
                            ? BigDecimal.ZERO
                            : item.getReturnedQuantity();
                    return returned.compareTo(item.getQuantity()) >= 0;
                });
        User returnUser = currentUser(updatedBy);
        if (returnUser != null) {
            BigDecimal returnTotal = BigDecimal.ZERO;
            for (SaleReturnItemRequest requestItem : request.items()) {
                SaleItems saleItem = sale.getItems().stream()
                        .filter(item -> item.getId().equals(requestItem.saleItemId()))
                        .findFirst()
                        .orElse(null);
                if (saleItem != null && saleItem.getPrice() != null) {
                    returnTotal = returnTotal.add(saleItem.getPrice().multiply(requestItem.quantity()));
                }
            }
            final BigDecimal finalReturnTotal = returnTotal;
            cashRegisterRepository.findOpenRegisterByUserId(returnUser.getId()).ifPresent(register -> {
                BigDecimal currentReturns = register.getTotalReturns() != null ? register.getTotalReturns() : BigDecimal.ZERO;
                register.setTotalReturns(currentReturns.add(finalReturnTotal));
                cashRegisterRepository.save(register);
            });
        }
        sale.setSaleStatus(fullyReturned ? Constants.RETURNED : Constants.PARTIAL_RETURNED);
        sale.setUpdatedBy(updatedBy);
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    @Transactional
    public void delete(Long id, String deletedBy) {
        Sales sale = findById(id);
        if (sale.getSaleStatus() != null && (COMPLETED.equalsIgnoreCase(sale.getSaleStatus()) || PARTIAL_RETURNED.equalsIgnoreCase(sale.getSaleStatus()))) {
            stockService.restoreSaleStock(sale.getStore().getId(), sale.getId(), String.valueOf(sale.getNo()), sale.getItems(), deletedBy);
        }
        sale.setDeletedBy(deletedBy);
        sale.setDeletedAt(LocalDateTime.now());
        saleRepository.save(sale);
    }

    @Override
    @Transactional(readOnly = true)
    public Sales findById(Long id) {
        return saleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sale", id));
    }

    private void replaceItems(Sales sale, List<SaleItemRequest> requests) {
        List<SaleItems> items = new ArrayList<>();
        for (SaleItemRequest request : requests) {
            var productId = productService.findById(request.productId());
            SaleItems item = new SaleItems();
            item.setSales(sale);
            item.setProduct(productId);
            item.setQuantity(request.quantity());
            item.setPrice(BigDecimal.valueOf(request.price()));
            item.setUnitCostPrice(resolveHistoricalUnitCostPrice(productId.getId(), request.quantity(), request.serialNumberIds()));
            item.setItemDiscount(BigDecimal.valueOf(request.itemDiscount() == null ? 0D : request.itemDiscount()));
            item.setSubTotal(item.getQuantity().multiply(item.getPrice()).subtract(item.getItemDiscount()));
            item.setProductSerialIds(request.serialNumberIds());
            item.setReturnedQuantity(BigDecimal.ZERO);
            items.add(item);
        }
        if (sale.getItems() == null) {
            sale.setItems(items);
        } else {
            sale.getItems().clear();
            sale.getItems().addAll(items);
        }
    }

    private BigDecimal resolveHistoricalUnitCostPrice(Long productId, BigDecimal quantity, List<Long> serialIds) {
        if (serialIds != null && !serialIds.isEmpty()) {
            List<BigDecimal> serialCosts = productSerialsRepository.findAllById(serialIds).stream()
                    .map(serial -> serial.getCost() != null ? serial.getCost() : serial.getProduct() != null ? serial.getProduct().getCostPrice() : null)
                    .filter(Objects::nonNull)
                    .toList();
            if (!serialCosts.isEmpty()) {
                BigDecimal totalCost = serialCosts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal divisor = quantity != null && quantity.compareTo(BigDecimal.ZERO) > 0
                        ? quantity
                        : BigDecimal.valueOf(serialCosts.size());
                return totalCost.divide(divisor, 4, java.math.RoundingMode.HALF_UP);
            }
        }

        var product = productService.findById(productId);
        return product.getCostPrice() != null ? product.getCostPrice() : BigDecimal.ZERO;
    }

    private void calculateTotalsAndPaymentStatus(Sales sale) {
        BigDecimal total = sale.getItems().stream()
                .map(SaleItems::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discount = BigDecimal.valueOf(sale.getDiscount() == null ? 0D : sale.getDiscount());
        BigDecimal grandTotal = total.subtract(discount);

        sale.setGrandTotal(grandTotal.doubleValue());
        BigDecimal paid = BigDecimal.valueOf(sale.getPaidAmount() == null ? 0D : sale.getPaidAmount());
        sale.setReturnAmount(paid.subtract(grandTotal).max(BigDecimal.ZERO).doubleValue());
        sale.setPaymentStatus(paymentStatus(paid, grandTotal));
    }

    private String paymentStatus(BigDecimal paid, BigDecimal grandTotal) {
        if (paid.signum() == 0) return PENDING;
        if (paid.compareTo(grandTotal) < 0) return PARTIAL;
        return PAID;
    }

    private User currentUser(String actor) {
        if (actor == null || "system".equalsIgnoreCase(actor)) {
            return null;
        }
        return userService.getCurrentUser();
    }
}