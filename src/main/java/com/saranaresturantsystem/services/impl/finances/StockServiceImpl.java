package com.saranaresturantsystem.services.impl.finances;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.dto.response.inventory.StockResponse;
import com.saranaresturantsystem.entities.catalog.ProductSerials;
import com.saranaresturantsystem.entities.inventory.Stock;
import com.saranaresturantsystem.entities.inventory.Stores;
import com.saranaresturantsystem.entities.purchase.PurchaseItem;
import com.saranaresturantsystem.entities.sales.SaleItems;
import com.saranaresturantsystem.execption.DuplicateResourceException;
import com.saranaresturantsystem.execption.InsufficientStockException;
import com.saranaresturantsystem.mappers.inventory.StockMapper;
import com.saranaresturantsystem.repository.Inventory.StockRepository;
import com.saranaresturantsystem.repository.Inventory.StoreRepsoitory;
import com.saranaresturantsystem.repository.catalog.ProductSerialsRepository;
import com.saranaresturantsystem.services.interfaces.catalog.ProductService;
import com.saranaresturantsystem.services.interfaces.inventory.InventoryService;
import com.saranaresturantsystem.services.interfaces.inventory.StockService;
import com.saranaresturantsystem.specification.inventory.stock.StockFilter;
import com.saranaresturantsystem.specification.inventory.stock.StockSpec;
import com.saranaresturantsystem.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.saranaresturantsystem.constants.Constants.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class StockServiceImpl implements StockService {
    private final ProductSerialsRepository productSerialsRepository;
    private final StockRepository stockRepository;
    private final StoreRepsoitory storeRepository;
    private final ProductService productService;
    private final InventoryService inventoryService;
    private  final ObjectMapper objectMapper  ;
    private  final StockMapper stockMapper ;
//    private  final LowStockPublisher lowStockPublisher;
    @Override
    public Page<StockResponse> findAll(Map<String, String> params) {
        StockFilter stockFilter = objectMapper.convertValue(params ,StockFilter.class);
        Pageable page  = PageUtil.fromParams(params);
        Specification<Stock> spec = StockSpec.filter(stockFilter);
        return  stockRepository.findAll(spec, page).map(stockMapper::toResponse);
    }





    // -------------------------------------------------------------------------
    // PURCHASE — receive goods into stock
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void processPurchaseStock(Long storeId, Long purchaseId, String referenceNo,
                                     Long productId, BigDecimal quantity, BigDecimal cost, BigDecimal price ,
                                     List<String> serialNumbers, PurchaseItem purchaseItem) {
        Long targetStoreId = resolveStoreId(storeId);

        if (serialNumbers != null && !serialNumbers.isEmpty()) {

          // save serial
            for (String barcode : serialNumbers) {
                if (barcode != null && productSerialsRepository.existsByBarcode(barcode)) {
                    throw new DuplicateResourceException(
                            "Serial / barcode '" + barcode + "' already exists in inventory");
                }
                ProductSerials serial = new ProductSerials();
                serial.setProduct(productService.findById(productId));
                serial.setBarcode(barcode);
                serial.setCost(cost);
                serial.setPrice(price);
                serial.setQuantity(BigDecimal.ONE);
                serial.setStoreId(targetStoreId);
                serial.setPurchaseId(purchaseId);
                serial.setPurchaseItem(purchaseItem);
                serial.setStatus(AVAILABLE);
                productSerialsRepository.save(serial);
            }

            BigDecimal serialCount = BigDecimal.valueOf(serialNumbers.size());
            adjustStock(productId, targetStoreId, serialCount);

            inventoryService.recordTransaction(productId, targetStoreId, serialCount,
                    PURCHASE, purchaseId, "Purchase Ref: " + referenceNo);

        } else {
            // ── Bulk / non-serial path ───────────────────────────────────────
            List<ProductSerials> existing = productSerialsRepository
                    .findByProductIdAndStoreIdAndStatusOrderByIdAsc(productId, targetStoreId, AVAILABLE);

            ProductSerials serial = new ProductSerials();
            if (!existing.isEmpty()) {
                serial = existing.getFirst();
                BigDecimal newQty = safeQty(serial.getQuantity()).add(quantity);
                serial.setQuantity(newQty);
                serial.setCost(cost);
            } else {
                serial.setProduct(productService.findById(productId));
                serial.setCost(cost);
                serial.setPrice(price);
                serial.setQuantity(quantity);
                serial.setStoreId(targetStoreId);
                serial.setPurchaseId(purchaseId);
                serial.setPurchaseItem(purchaseItem);
                serial.setStatus(AVAILABLE);
            }
            productSerialsRepository.save(serial);
            adjustStock(productId, targetStoreId, quantity);
            inventoryService.recordTransaction(productId, targetStoreId,
                    quantity, PURCHASE,
                    purchaseId,
                    "Purchase Ref: " + referenceNo);
        }
    }

    // -------------------------------------------------------------------------
    // CANCEL PURCHASE — reverse all serials back out
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void reversePurchaseStock(Long storeId, Long purchaseId, String referenceNo, String updatedBy) {
        Long targetStoreId = resolveStoreId(storeId);
        List<ProductSerials> serials = productSerialsRepository.findByPurchaseId(purchaseId);

        for (ProductSerials serial : serials) {
            serial.setStatus(CANCELLED);
            BigDecimal qty = safeQty(serial.getQuantity());
            adjustStock(serial.getProduct().getId(), targetStoreId, qty.negate());
            inventoryService.recordTransaction(
                    serial.getProduct().getId(), targetStoreId, qty.negate(),
                    CANCEL_PURCHASE, purchaseId,
                    "Cancelled Purchase Ref: " + referenceNo + " by " + updatedBy);
        }
        productSerialsRepository.saveAll(serials);
    }

    // -------------------------------------------------------------------------
    // SALE — deduct stock
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void deductSaleStock(Long storeId, Long saleId, String saleNo,
                                Long productId, BigDecimal quantity,
                                List<Long> serialIds, String updatedBy) {
        Long targetStoreId = resolveStoreId(storeId);

        if (serialIds != null && !serialIds.isEmpty()) {
            // ── Serialised path ──────────────────────────────────────────────
            int expectedCount;
            try {
                expectedCount = quantity.intValueExact();
            } catch (ArithmeticException ex) {
                throw new InsufficientStockException(
                        "Serial IDs require an integer quantity for product " + productId);
            }
            if (serialIds.size() != expectedCount) {
                throw new InsufficientStockException(
                        "Product " + productId + " requires " + expectedCount
                                + " serial IDs, but " + serialIds.size() + " were provided");
            }
            List<ProductSerials> serials = serialIds.stream()
                    .map(id -> findSerialById(id, productId, targetStoreId))
                    .collect(Collectors.toList());
            for (ProductSerials serial : serials) {
                if (safeQty(serial.getQuantity()).compareTo(BigDecimal.ONE) < 0) {
                    throw new InsufficientStockException("Stock not enough for product id: " + productId);
                }
                serial.setQuantity(BigDecimal.ZERO);
                serial.setStatus(SOLD);
            }
            productSerialsRepository.saveAll(serials);

        } else {
            // ── Bulk / non-serial path ───────────────────────────────────────
            List<ProductSerials> serials = productSerialsRepository
                    .findByProductIdAndStoreIdAndStatusOrderByIdAsc(productId, targetStoreId, AVAILABLE);
            BigDecimal available = serials.stream()
                    .map(s -> safeQty(s.getQuantity()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (available.compareTo(quantity) < 0) {
                throw new InsufficientStockException("Stock not enough for product id: " + productId);
            }
            BigDecimal remaining = quantity;
            for (ProductSerials serial : serials) {
                if (remaining.signum() <= 0) break;
                BigDecimal current = safeQty(serial.getQuantity());
                BigDecimal deducted = current.min(remaining);
                BigDecimal newQty = current.subtract(deducted);
                serial.setQuantity(newQty);
                if (newQty.signum() == 0) serial.setStatus(OUT_OF_STOCK);
                remaining = remaining.subtract(deducted);
            }
            productSerialsRepository.saveAll(serials);
        }

        adjustStock(productId, targetStoreId, quantity.negate());
        inventoryService.recordTransaction(productId, targetStoreId, quantity.negate(),
                SALE, saleId, "Sale #" + saleNo + " by " + updatedBy);
    }

    // -------------------------------------------------------------------------
    // RETURN / CANCEL SALE — restore stock
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void restoreSaleStock(Long storeId, Long saleId, String saleNo,
                                 List<SaleItems> items, String updatedBy) {
        if (items == null || items.isEmpty()) return;
        Long targetStoreId = resolveStoreId(storeId);

        for (SaleItems item : items) {
            var product = productService.findById(item.getProduct().getId());
            BigDecimal quantity = item.getQuantity();
            List<Long> serialIds = item.getProductSerialIds();

            if (serialIds != null && !serialIds.isEmpty()) {
                // Serialised path
                List<ProductSerials> serials = productSerialsRepository.findAllById(serialIds);
                for (ProductSerials serial : serials) {
                    serial.setQuantity(BigDecimal.ONE);
                    serial.setStatus(AVAILABLE);
                }
                productSerialsRepository.saveAll(serials);
            } else {

                // none serial path
                List<ProductSerials> existing = productSerialsRepository
                        .findByProductIdAndStoreIdAndStatusOrderByIdAsc(product.getId(), targetStoreId, AVAILABLE);
                ProductSerials serial;
                if (!existing.isEmpty()) {
                    serial = existing.getFirst();
                    serial.setQuantity(safeQty(serial.getQuantity()).add(quantity));
                } else {
                    serial = new ProductSerials();
                    serial.setProduct(product);
                    serial.setQuantity(quantity);
                    serial.setStoreId(targetStoreId);
                    serial.setStatus(AVAILABLE);
                }
                productSerialsRepository.save(serial);
            }

            adjustStock(product.getId(), targetStoreId, quantity);
            inventoryService.recordTransaction(product.getId(), targetStoreId, quantity,
                    RETURN_SALE, saleId, "Returned Sale #" + saleNo + " by " + updatedBy);
        }
    }


    // ADJUST STOCK AGGREGATE (public — can be called directly if needed)

    @Transactional
    @Override
    public void adjustStock(Long productId, Long storeId, BigDecimal delta) {
        Stock stock = stockRepository.findByProductIdAndStoresId(productId, storeId)
                .orElseGet(() -> buildNewStock(productId, storeId));

        stock.setQuantity(safeQty(stock.getQuantity()).add(delta));
        stockRepository.save(stock);
//        lowStockPublisher.checkAndPublish(stock);
        log.debug("adjustStock: product={} store={} delta={} → qty={}",
                productId, storeId, delta, stock.getQuantity());
    }


    // PRIVATE HELPERS

    private Stock buildNewStock(Long productId, Long storeId) {
        Stores store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found: " + storeId));
        var product = productService.findById(productId);
        Stock s = new Stock();
        s.setProduct(product);
        s.setStores(store);
        s.setQuantity(BigDecimal.ZERO);
        if (product.getReorderLevel() != null) {
            s.setAlertQuantity(BigDecimal.valueOf(product.getReorderLevel()));
        }
        return s;
    }

    private ProductSerials findSerialById(Long serialId, Long productId, Long storeId) {
        return productSerialsRepository.findById(serialId)
                .filter(s -> s.getProduct().getId().equals(productId))
                .filter(s -> s.getStoreId().equals(storeId))
                .filter(s -> AVAILABLE.equals(s.getStatus()))
                .orElseThrow(() -> new InsufficientStockException(
                        "Stock not enough for product id: " + productId));
    }

    /** Null-safe quantity coerce — treats null as zero. */
    private BigDecimal safeQty(BigDecimal qty) {
        return qty != null ? qty : BigDecimal.ZERO;
    }

    /** Default to store 1 when storeId is not provided. */
    private Long resolveStoreId(Long storeId) {
        return storeId != null ? storeId : 1L;
    }


}
