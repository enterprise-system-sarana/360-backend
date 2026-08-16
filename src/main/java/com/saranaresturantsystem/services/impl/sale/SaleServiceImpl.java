package com.saranaresturantsystem.services.impl.sale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.dto.request.sales.SaleItemRequest;
import com.saranaresturantsystem.dto.request.sales.SaleRequest;
import com.saranaresturantsystem.dto.response.sales.SaleResponse;
import com.saranaresturantsystem.entities.sales.SaleItems;
import com.saranaresturantsystem.entities.sales.Sales;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.sale.SaleMapper;
import com.saranaresturantsystem.repository.sales.SaleRepository;
import com.saranaresturantsystem.services.interfaces.catalog.ProductService;
import com.saranaresturantsystem.services.interfaces.inventory.StockService;
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

import static com.saranaresturantsystem.constants.Constants.*;
import static com.saranaresturantsystem.constants.Constants.PAID;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final SaleMapper saleMapper;
    private final ObjectMapper objectMapper;
    private  final ProductService productService ;
    private  final StockService stockService ;
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
        sale.setSaleStatus(COMPLETED);
        sale.setCreatedBy(createdBy);

        replaceItems(sale, request.items());
        calculateTotalsAndPaymentStatus(sale);

        Sales savedSale = saleRepository.save(sale);

        for (SaleItems item : savedSale.getItems()) {
            stockService.deductSaleStock(
                    savedSale.getStoreId(),
                    savedSale.getId(),
                    String.valueOf(savedSale.getNo()),
                    item.getProduct().getId(),
                    item.getQuantity(),
                    item.getProductSerialIds(),
                    createdBy
            );
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
        if (sale.getSaleStatus().equals(Constants.PENDING)) {
            throw new IllegalArgumentException("Only PENDING sales can be completed");
        }

        for (SaleItems item : sale.getItems()) {
            var productId = productService.findById(item.getProduct().getId());
            stockService.deductSaleStock(
                    sale.getStoreId(),
                    sale.getId(),
                    String.valueOf(sale.getNo()),
                    productId.getId(),
                    item.getQuantity(),
                    item.getProductSerialIds(),
                    updatedBy
            );
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
    public SaleResponse returnSale(Long id, String updatedBy) {
        Sales sale = findById(id);
        if (sale.getSaleStatus().equals(COMPLETED)) {
            stockService.restoreSaleStock(
                    sale.getStoreId(),
                    sale.getId(),
                    String.valueOf(sale.getNo()),
                    sale.getItems(),
                    updatedBy
            );
        }
        sale.setSaleStatus(Constants.RETURNED);
        sale.setUpdatedBy(updatedBy);
        return saleMapper.toResponse(saleRepository.save(sale));
    }


    @Override
    @Transactional
    public void delete(Long id, String deletedBy) {
        Sales sale = findById(id);
        if (sale.getSaleStatus().equals(COMPLETED)) {
            stockService.restoreSaleStock(
                    sale.getStoreId(),
                    sale.getId(),
                    String.valueOf(sale.getNo()),
                    sale.getItems(),
                    deletedBy
            );
        }
        sale.setDeletedBy(deletedBy);
        saleRepository.save(sale);
    }

    @Override
    @Transactional(readOnly = true)
    public Sales findById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", id));
    }

    private int nextSaleNumber() {
        Integer lastNo = saleRepository.findMaxNo();
        return lastNo == null ? 1 : lastNo + 1;
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
            items.add(item);
        }
        sale.setItems(items);
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
}