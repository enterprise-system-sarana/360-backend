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
    import com.saranaresturantsystem.entities.sales.Payment;
    import com.saranaresturantsystem.entities.sales.Sales;
    import com.saranaresturantsystem.entities.users.User;
    import com.saranaresturantsystem.execption.ResourceNotFoundException;
    import com.saranaresturantsystem.mappers.sale.SaleMapper;
    import com.saranaresturantsystem.repository.sales.SaleRepository;
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

    import static com.saranaresturantsystem.constants.Constants.*;
    import static com.saranaresturantsystem.constants.Constants.PAID;

    @Service
    @RequiredArgsConstructor
    public class SaleServiceImpl implements SaleService {

        private final SaleRepository saleRepository;
        private final PaymentRepository paymentRepository;
        private final SaleMapper saleMapper;
        private final ObjectMapper objectMapper;
        private  final ProductService productService ;
        private  final StockService stockService ;
        private  final InvoiceNumberService invoiceNumberService ;
        private  final StoreService storeService ;
        private  final InventoryService inventoryService ;
        private  final BankService bankService;
        private  final CustomerService customerService ;
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
            sale.setUser(currentUser(createdBy));
            sale.setNo(invoiceNumberService.generate("POS"));
            sale.setDate(LocalDateTime.now());
            sale.setSaleStatus(COMPLETED);
            sale.setCreatedBy(createdBy);
            replaceItems(sale, request.items());
            calculateTotalsAndPaymentStatus(sale);
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

            savePayment(savedSale, request.paidAmount(), request.paymentMethod(), createdBy);
            return saleMapper.toResponse(savedSale);
        }

        private void savePayment(Sales sale, Double paidAmount, String paymentMethod, String createdBy) {
            if (paidAmount == null || paidAmount <= 0) {
                return;
            }

            Payment payment = new Payment();
            payment.setPaymentNo(invoiceNumberService.generate("PAY"));
            payment.setTransactionNo("SALE");
            payment.setSales(sale);
            payment.setBanks(sale.getBanks());
            payment.setPaymentMethod(paymentMethod);
            payment.setAmount(BigDecimal.valueOf(paidAmount));
            payment.setPaymentDate(LocalDateTime.now());
            payment.setStatus(paymentStatus(
                BigDecimal.valueOf(paidAmount),
                BigDecimal.valueOf(sale.getGrandTotal())
            ));
            payment.setCreatedBy(createdBy);
            payment.setUser(sale.getUser());
            paymentRepository.save(payment);
        }

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
            calculateTotalsAndPaymentStatus(sale);
            return saleMapper.toResponse(saleRepository.save(sale));
        }

        @Override
        @Transactional
        public SaleResponse complete(Long id, String updatedBy) {
            Sales sale = findById(id);
            if (!sale.getSaleStatus().equals(Constants.PENDING)) {
                throw new IllegalArgumentException("Only PENDING sales can be completed");
            }

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

            if (sale.getBanks() != null) {
                for (int i = 0; i < sale.getItems().size(); i++) {
                    SaleItems item = sale.getItems().get(i);
                    inventoryService.recordBankTransaction(
                            null,
                            sale.getId(),
                            sale.getBanks().getId(),
                            null,
                            item.getSubTotal(),
                            sale.getNo() + "-" + (i + 1),
                            SALE,
                            "Sale of product ID " + item.getProduct().getId() + " with quantity " + item.getQuantity()
                    );
                }
            }

            sale.setSaleStatus(COMPLETED);
            sale.setUpdatedBy(updatedBy);
            return saleMapper.toResponse(saleRepository.save(sale));
        }

        @Override
        @Transactional
        public SaleResponse cancel(Long id, String updatedBy) {
            Sales sale = findById(id);
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
            sale.setSaleStatus(fullyReturned ? Constants.RETURNED : Constants.PARTIAL_RETURNED);
            sale.setUpdatedBy(updatedBy);
            return saleMapper.toResponse(saleRepository.save(sale));
        }


        @Override
        @Transactional
        public void delete(Long id, String deletedBy) {
            Sales sale = findById(id);
            if (sale.getSaleStatus().equals(COMPLETED)) {
                stockService.restoreSaleStock(sale.getStore().getId(), sale.getId(), String.valueOf(sale.getNo()), sale.getItems(), deletedBy);
            }
            sale.setDeletedBy(deletedBy);
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
    //            validateSerialIds(request);
                SaleItems item = new SaleItems();
                item.setSales(sale);
                item.setProduct(productId);
                item.setQuantity(request.quantity());
                item.setPrice(BigDecimal.valueOf(request.price()));
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