package com.saranaresturantsystem.services.impl.sale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.dto.request.sales.SaleItemRequest;
import com.saranaresturantsystem.dto.request.sales.SaleRequest;
import com.saranaresturantsystem.dto.response.sales.SaleResponse;
import com.saranaresturantsystem.entities.catalog.ProductSerials;
import com.saranaresturantsystem.entities.inventory.Inventory_Transactions;
import com.saranaresturantsystem.entities.sales.SaleItems;
import com.saranaresturantsystem.entities.sales.Sales;
import com.saranaresturantsystem.enums.PaymentStatus;
import com.saranaresturantsystem.enums.SaleStatus;
import com.saranaresturantsystem.execption.InsufficientStockException;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.sale.SaleMapper;
import com.saranaresturantsystem.repository.Inventory.InventoryTransactionsRepository;
import com.saranaresturantsystem.repository.catalog.ProductSerialsRepository;
import com.saranaresturantsystem.repository.sales.SaleRepository;
import com.saranaresturantsystem.services.interfaces.sales.SaleService;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private static final String AVAILABLE = "AVAILABLE";
    private static final String OUT_OF_STOCK = "OUT_OF_STOCK";

    private final SaleRepository saleRepository;
    private final ProductSerialsRepository productSerialsRepository;
    private final InventoryTransactionsRepository inventoryTransactionsRepository;
    private final SaleMapper saleMapper;
    private final ObjectMapper objectMapper;

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
        Sales sale = saleMapper.toEntity(request);
        sale.setNo(nextSaleNumber());
        sale.setDate(LocalDateTime.now());
        sale.setDeleteFlag(0);
        sale.setSaleStatus(SaleStatus.COMPLETED);
        sale.setCreatedBy(createdBy);

        replaceItems(sale, request.items());
        calculateTotalsAndPaymentStatus(sale);

        Sales savedSale = saleRepository.save(sale);

        for (SaleItems item : savedSale.getItems()) {
            deductStock(item.getProductId(), savedSale.getStoreId(), item.getQuantity(), item.getProductSerialIds());
            recordInventoryTransaction(item, savedSale, "SALE", createdBy);
        }

        return saleMapper.toResponse(savedSale);
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
        sale.setUpdatedBy(updatedBy);
        replaceItems(sale, request.items());
        calculateTotalsAndPaymentStatus(sale);
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    @Transactional
    public SaleResponse complete(Long id, String updatedBy) {
        Sales sale = findById(id);
        if (sale.getSaleStatus() != SaleStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING sales can be completed");
        }

        for (SaleItems item : sale.getItems()) {
            deductStock(item.getProductId(), sale.getStoreId(), item.getQuantity(), item.getProductSerialIds());
            recordInventoryTransaction(item, sale, "SALE", updatedBy);
        }

        sale.setSaleStatus(SaleStatus.COMPLETED);
        sale.setUpdatedBy(updatedBy);
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    @Transactional
    public SaleResponse cancel(Long id, String updatedBy) {
        Sales sale = findById(id);
        sale.setSaleStatus(SaleStatus.CANCELLED);
        sale.setUpdatedBy(updatedBy);
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    @Transactional
    public SaleResponse returnSale(Long id, String updatedBy) {
        Sales sale = findById(id);
        sale.setSaleStatus(SaleStatus.RETURNED);
        sale.setUpdatedBy(updatedBy);
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    @Transactional
    public void delete(Long id, String deletedBy) {
        Sales sale = findById(id);
        sale.setDeleteFlag(1);
        sale.setDeletedBy(deletedBy);
        saleRepository.save(sale);
    }

    @Override
    @Transactional(readOnly = true)
    public Sales findById(Long id) {
        return saleRepository.findById(id)
                .filter(sale -> sale.getDeleteFlag() == null || sale.getDeleteFlag() == 0)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", id));
    }

    private int nextSaleNumber() {
        Integer lastNo = saleRepository.findMaxNo();
        return lastNo == null ? 1 : lastNo + 1;
    }

    private void replaceItems(Sales sale, List<SaleItemRequest> requests) {
        List<SaleItems> items = new ArrayList<>();
        for (SaleItemRequest request : requests) {
            validateSerialIds(request);
            SaleItems item = new SaleItems();
            item.setSales(sale);
            item.setProductId(request.productId());
            item.setQuantity(request.quantity());
            item.setPrice(BigDecimal.valueOf(request.price()));
            item.setItemDiscount(BigDecimal.valueOf(request.itemDiscount() == null ? 0D : request.itemDiscount()));
            item.setSubTotal(item.getQuantity().multiply(item.getPrice()).subtract(item.getItemDiscount()));
            item.setProductSerialIds(request.serialNumberIds());
            items.add(item);
        }
        sale.setItems(items);
    }

    private void validateSerialIds(SaleItemRequest request) {
        List<Long> serialIds = request.serialNumberIds();
        if (serialIds == null || serialIds.isEmpty()) {
            return;
        }

        try {
            int expectedCount = request.quantity().intValueExact();
            if (serialIds.size() != expectedCount) {
                throw new InsufficientStockException(
                        "Product " + request.productId() + " requires " + expectedCount + " serial IDs, but "
                                + serialIds.size() + " were provided");
            }
        } catch (ArithmeticException ex) {
            throw new InsufficientStockException(
                    "Serial IDs require an integer quantity for product " + request.productId());
        }

        long distinctCount = serialIds.stream().distinct().count();
        if (distinctCount != serialIds.size()) {
            throw new InsufficientStockException(
                    "Duplicate serial IDs are not allowed for product " + request.productId());
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

    private PaymentStatus paymentStatus(BigDecimal paid, BigDecimal grandTotal) {
        if (paid.signum() == 0) return PaymentStatus.PENDING;
        if (paid.compareTo(grandTotal) < 0) return PaymentStatus.PARTIAL;
        return PaymentStatus.PAID;
    }

    private void deductStock(Long productId, Long storeId, BigDecimal quantity, List<Long> serialIds) {
        Long targetStoreId = storeId != null ? storeId : 1L;

        if (serialIds != null && !serialIds.isEmpty()) {
            int expectedCount;
            try {
                expectedCount = quantity.intValueExact();
            } catch (ArithmeticException ex) {
                throw new InsufficientStockException("Serial IDs require an integer quantity for product " + productId);
            }
            if (serialIds.size() != expectedCount) {
                throw new InsufficientStockException(
                        "Product " + productId + " requires " + expectedCount + " serial IDs, but "
                                + serialIds.size() + " were provided");
            }

            List<ProductSerials> serials = serialIds.stream()
                    .map(id -> findSerialById(id, productId, targetStoreId))
                    .collect(Collectors.toList());

            for (ProductSerials serial : serials) {
                if (serial.getQuantity() == null || serial.getQuantity().compareTo(BigDecimal.ONE) < 0) {
                    throw new InsufficientStockException("Stock not enough for product id: " + productId);
                }
                serial.setQuantity(BigDecimal.ZERO);
                serial.setStatus(OUT_OF_STOCK);
            }

            productSerialsRepository.saveAll(serials);
            return;
        }

        List<ProductSerials> serials = productSerialsRepository
                .findByProductIdAndStoreIdAndDeletedAndStatusOrderByIdAsc(productId, targetStoreId, 0, AVAILABLE);

        BigDecimal available = serials.stream().map(ProductSerials::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (available.compareTo(quantity) < 0) {
            throw new InsufficientStockException("Stock not enough for product id: " + productId);
        }

        BigDecimal remaining = quantity;
        for (ProductSerials serial : serials) {
            if (remaining.signum() <= 0) break;
            BigDecimal deducted = serial.getQuantity().min(remaining);
            serial.setQuantity(serial.getQuantity().subtract(deducted));
            if (serial.getQuantity().signum() == 0) {
                serial.setStatus(OUT_OF_STOCK);
            }
            remaining = remaining.subtract(deducted);
        }
        productSerialsRepository.saveAll(serials);
    }

    private ProductSerials findSerialById(Long serialId, Long productId, Long storeId) {
        return productSerialsRepository.findById(serialId)
                .filter(serial -> serial.getProductId().equals(productId))
                .filter(serial -> serial.getStoreId().equals(storeId))
                .filter(serial -> serial.getDeleted() == 0)
                .filter(serial -> AVAILABLE.equals(serial.getStatus()))
                .orElseThrow(() -> new InsufficientStockException("Stock not enough for product id: " + productId));
    }

    private void recordInventoryTransaction(SaleItems item, Sales sale, String type, String updatedBy) {
        Inventory_Transactions transaction = new Inventory_Transactions();
        transaction.setProductId(item.getProductId());
        transaction.setStoreId(sale.getStoreId() != null ? sale.getStoreId() : 1L);
        transaction.setQuantity(item.getQuantity().negate());
        transaction.setType(type);
        transaction.setReferenceId(sale.getId());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setNotes("Sale #" + sale.getNo() + " by " + updatedBy);
        inventoryTransactionsRepository.save(transaction);
    }
}