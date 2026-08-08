package com.saranaresturantsystem.services.impl.sale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.dto.request.sales.SaleItemRequest;
import com.saranaresturantsystem.dto.request.sales.SaleRequest;
import com.saranaresturantsystem.dto.response.sales.SaleResponse;
import com.saranaresturantsystem.entities.catalog.ProductSerials;
import com.saranaresturantsystem.entities.sales.SaleItems;
import com.saranaresturantsystem.entities.sales.Sales;
import com.saranaresturantsystem.enums.SaleStatus;
import com.saranaresturantsystem.execption.InsufficientStockException;
import com.saranaresturantsystem.mappers.sale.SaleMapper;
import com.saranaresturantsystem.repository.Inventory.InventoryTransactionsRepository;
import com.saranaresturantsystem.repository.Inventory.StoreRepsoitory;
import com.saranaresturantsystem.repository.catalog.ProductSerialsRepository;
import com.saranaresturantsystem.repository.customer.CustomerRepository;
import com.saranaresturantsystem.repository.sales.SaleRepository;
import com.saranaresturantsystem.services.interfaces.catalog.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaleServiceImplTest {

    @Mock private SaleRepository saleRepository;
    @Mock private ProductSerialsRepository productSerialsRepository;
    @Mock private InventoryTransactionsRepository inventoryTransactionsRepository;
    @Mock private StoreRepsoitory storeRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private ProductService productService;
    @Mock private SaleMapper saleMapper;
    @Mock private ObjectMapper objectMapper;
    @InjectMocks private SaleServiceImpl saleService;

    @Test
    void completeDeductsStockRecordsTransactionAndMarksSaleCompleted() {
        Sales sale = pendingSale(new BigDecimal("3"));
        ProductSerials serial = serial(new BigDecimal("5"));
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
        when(productSerialsRepository.findByProductIdAndStoreIdAndDeletedAndStatusOrderByIdAsc(10L, 2L, 0, "AVAILABLE"))
                .thenReturn(List.of(serial));
        when(saleRepository.save(sale)).thenReturn(sale);
        when(saleMapper.toResponse(sale)).thenReturn(response());

        saleService.complete(1L, "cashier");

        assertEquals(SaleStatus.COMPLETED, sale.getSaleStatus());
        assertEquals(new BigDecimal("2"), serial.getQuantity());
        verify(productSerialsRepository).saveAll(List.of(serial));
        verify(inventoryTransactionsRepository).save(any());
        verify(saleRepository).save(sale);
    }

    @Test
    void completeRejectsSaleWhenAvailableStockIsInsufficient() {
        Sales sale = pendingSale(new BigDecimal("3"));
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
        when(productSerialsRepository.findByProductIdAndStoreIdAndDeletedAndStatusOrderByIdAsc(10L, 2L, 0, "AVAILABLE"))
                .thenReturn(List.of(serial(new BigDecimal("2"))));

        assertThrows(InsufficientStockException.class, () -> saleService.complete(1L, "cashier"));

        assertEquals(SaleStatus.PENDING, sale.getSaleStatus());
        verify(inventoryTransactionsRepository, never()).save(any());
        verify(saleRepository, never()).save(sale);
    }

    @Test
    void completeDeductsSpecificSerialIdsAndZerosQuantities() {
        Sales sale = new Sales();
        sale.setStoreId(2L);
        sale.setId(1L);
        sale.setSaleStatus(SaleStatus.PENDING);
        sale.setItems(new java.util.ArrayList<>(List.of(serialItem(new BigDecimal("2"), List.of(5L, 6L), 10L))));

        ProductSerials serial1 = serial(new BigDecimal("1"));
        serial1.setId(5L);
        ProductSerials serial2 = serial(new BigDecimal("1"));
        serial2.setId(6L);

        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
        when(saleRepository.save(sale)).thenReturn(sale);
        when(productSerialsRepository.findById(5L)).thenReturn(Optional.of(serial1));
        when(productSerialsRepository.findById(6L)).thenReturn(Optional.of(serial2));
        when(saleMapper.toResponse(sale)).thenReturn(response());

        saleService.complete(1L, "cashier");

        assertEquals(BigDecimal.ZERO, serial1.getQuantity());
        assertEquals(BigDecimal.ZERO, serial2.getQuantity());
        assertEquals("OUT_OF_STOCK", serial1.getStatus());
        assertEquals("OUT_OF_STOCK", serial2.getStatus());
        verify(productSerialsRepository).saveAll(List.of(serial1, serial2));
        verify(inventoryTransactionsRepository).save(any());
        verify(saleRepository).save(sale);
    }

    @Test
    void createRejectsWhenSerialCountDoesNotMatchQuantity() {
        com.saranaresturantsystem.dto.request.sales.SaleRequest request = new com.saranaresturantsystem.dto.request.sales.SaleRequest(
                null, 2L, null, 0D, 0D, null,
                List.of(new com.saranaresturantsystem.dto.request.sales.SaleItemRequest(10L, new BigDecimal("3"), 10D, 0D, List.of(5L))));

        when(saleMapper.toEntity(request)).thenReturn(new Sales());

        assertThrows(InsufficientStockException.class, () -> saleService.create(request, "cashier"));
        verify(saleRepository, never()).save(any());
    }

    @Test
    void createRejectsDuplicateSerialIds() {
        SaleRequest request = new SaleRequest(
                null, 2L, null, 0D, 0D, null,
                List.of(new SaleItemRequest(10L, new BigDecimal("2"), 10D, 0D, List.of(5L, 5L))));

        when(saleMapper.toEntity(request)).thenReturn(new Sales());

        assertThrows(InsufficientStockException.class, () -> saleService.create(request, "cashier"));
        verify(saleRepository, never()).save(any());
    }

    private SaleItems serialItem(BigDecimal quantity, List<Long> serialIds, Long productId) {
        SaleItems item = new SaleItems();
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setPrice(BigDecimal.TEN);
        item.setSubTotal(quantity.multiply(BigDecimal.TEN));
        item.setProductSerialIds(serialIds);
        return item;
    }

    private Sales pendingSale(BigDecimal quantity) {
        SaleItems item = new SaleItems();
        item.setProductId(10L);
        item.setQuantity(quantity);
        item.setPrice(BigDecimal.TEN);
        item.setSubTotal(quantity.multiply(BigDecimal.TEN));

        Sales sale = new Sales();
        sale.setId(1L);
        sale.setNo(1);
        sale.setStoreId(2L);
        sale.setDeleteFlag(0);
        sale.setSaleStatus(SaleStatus.PENDING);
        sale.setItems(List.of(item));
        return sale;
    }

    private ProductSerials serial(BigDecimal quantity) {
        ProductSerials serial = new ProductSerials();
        serial.setId(5L);
        serial.setProductId(10L);
        serial.setStoreId(2L);
        serial.setDeleted(0);
        serial.setStatus("AVAILABLE");
        serial.setQuantity(quantity);
        return serial;
    }

    private SaleResponse response() {
        return new SaleResponse(1L, null, null, "1", 2L, null, null, null, null, null, null, null, null, List.of());
    }
}
