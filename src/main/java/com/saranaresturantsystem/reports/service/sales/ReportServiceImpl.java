package com.saranaresturantsystem.reports.service.sales;

import com.saranaresturantsystem.dto.response.sales.SaleResponse;
import com.saranaresturantsystem.entities.customer.Customer;
import com.saranaresturantsystem.entities.inventory.Stores;
import com.saranaresturantsystem.repository.Inventory.StoreRepsoitory;
import com.saranaresturantsystem.repository.customer.CustomerRepository;
import com.saranaresturantsystem.reports.dto.sales.SaleItemReportResponse;
import com.saranaresturantsystem.reports.dto.sales.SaleReportFilter;
import com.saranaresturantsystem.reports.dto.sales.SaleReportResponse;
import com.saranaresturantsystem.reports.dto.sales.SalesReportSummaryProjection;
import com.saranaresturantsystem.reports.entities.ViewSalesItemsReport;
import com.saranaresturantsystem.reports.entities.ViewSalesReport;
import com.saranaresturantsystem.reports.repository.ViewSalesItemsReportRepository;
import com.saranaresturantsystem.reports.repository.ViewSalesReportRepository;
import com.saranaresturantsystem.reports.specification.sales.ViewSalesItemsReportSpec;
import com.saranaresturantsystem.reports.specification.sales.ViewSalesReportSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ViewSalesReportRepository viewSalesReportRepository;
    private final ViewSalesItemsReportRepository viewSalesItemsReportRepository;
    private final StoreRepsoitory storeRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional(readOnly = true)
    public SaleReportResponse getSaleReport(SaleReportFilter filter) {
        LocalDateTime startDate = filter.startDate() != null ? filter.startDate().atStartOfDay() : null;
        LocalDateTime endDate = filter.endDate() != null ? filter.endDate().atTime(LocalTime.MAX) : null;

        String saleStatus = filter.saleStatus() != null && !filter.saleStatus().isBlank() ? filter.saleStatus().trim() : null;
        String paymentStatus = filter.paymentStatus() != null && !filter.paymentStatus().isBlank() ? filter.paymentStatus().trim() : null;

        // 1. High-performance aggregated metrics from database view
        SalesReportSummaryProjection summary = viewSalesReportRepository.getSummary(
                filter.storeId(),
                filter.customerId(),
                saleStatus,
                paymentStatus,
                startDate,
                endDate
        );

        double totalSalesAmount = summary != null && summary.getTotalSalesAmount() != null ? summary.getTotalSalesAmount().doubleValue() : 0.0;
        double totalDiscount = summary != null && summary.getTotalDiscount() != null ? summary.getTotalDiscount().doubleValue() : 0.0;
        double totalPaidAmount = summary != null && summary.getTotalPaidAmount() != null ? summary.getTotalPaidAmount().doubleValue() : 0.0;
        double totalCost = summary != null && summary.getTotalCost() != null ? summary.getTotalCost().doubleValue() : 0.0;
        double totalProfit = summary != null && summary.getTotalProfit() != null ? summary.getTotalProfit().doubleValue() : 0.0;
        long totalTransactions = summary != null && summary.getTotalTransactions() != null ? summary.getTotalTransactions() : 0L;

        // 2. Fetch list of sales from database view
        Specification<ViewSalesReport> spec = ViewSalesReportSpec.filter(filter);
        List<ViewSalesReport> viewSales = viewSalesReportRepository.findAll(spec);

        List<SaleResponse> saleResponses = viewSales.stream()
                .map(this::mapToSaleResponse)
                .toList();

        // 3. Store and Customer metadata
        Long storeId = filter.storeId();
        String storeName = null;
        if (storeId != null) {
            storeName = storeRepository.findById(storeId).map(Stores::getName).orElse(null);
        } else if (!viewSales.isEmpty()) {
            Set<String> storeNames = viewSales.stream().map(ViewSalesReport::getStoreName).filter(Objects::nonNull).collect(Collectors.toSet());
            if (storeNames.size() == 1) {
                storeName = storeNames.iterator().next();
                storeId = viewSales.get(0).getStoreId();
            } else if (storeNames.size() > 1) {
//                storeName = AllStores;
            }
        }

        Long customerId = filter.customerId();
        String customerName = null;
        if (customerId != null) {
            customerName = customerRepository.findById(customerId).map(Customer::getName).orElse(null);
        } else if (!viewSales.isEmpty()) {
            Set<String> customerNames = viewSales.stream().map(ViewSalesReport::getCustomerName).filter(Objects::nonNull).collect(Collectors.toSet());
            if (customerNames.size() == 1) {
                customerName = customerNames.iterator().next();
                customerId = viewSales.get(0).getCustomerId();
            } else if (customerNames.size() > 1) {
//                customerName = AllCustomers;
            }
        }

        if (startDate == null && !viewSales.isEmpty()) {
            startDate = viewSales.stream().map(ViewSalesReport::getSaleDate).filter(Objects::nonNull).min(LocalDateTime::compareTo).orElse(null);
        }
        if (endDate == null && !viewSales.isEmpty()) {
            endDate = viewSales.stream().map(ViewSalesReport::getSaleDate).filter(Objects::nonNull).max(LocalDateTime::compareTo).orElse(null);
        }

        return SaleReportResponse.builder()
                .totalSalesAmount(totalSalesAmount)
                .totalDiscount(totalDiscount)
                .totalPaidAmount(totalPaidAmount)
                .totalCost(totalCost)
                .totalProfit(totalProfit)
                .totalTransactions(totalTransactions)
                .storeId(storeId)
                .storeName(storeName)
                .customerId(customerId)
                .customerName(customerName)
                .startDate(startDate)
                .endDate(endDate)
                .sales(saleResponses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SaleItemReportResponse> getSalesItemsReport(SaleReportFilter filter, Pageable pageable) {
        Specification<ViewSalesItemsReport> spec = ViewSalesItemsReportSpec.filter(filter);

        return viewSalesItemsReportRepository.findAll(spec, pageable).map(item -> {
            double quantity = item.getQuantity() != null ? item.getQuantity().doubleValue() : 0.0;
            double returnedQuantity = item.getReturnedQuantity() != null ? item.getReturnedQuantity().doubleValue() : 0.0;
            double unitPrice = item.getUnitPrice() != null ? item.getUnitPrice().doubleValue() : 0.0;
            double unitCost = item.getUnitCost() != null ? item.getUnitCost().doubleValue() : 0.0;
            double itemDiscount = item.getItemDiscount() != null ? item.getItemDiscount().doubleValue() : 0.0;
            double subtotal = item.getSubTotal() != null ? item.getSubTotal().doubleValue() : (unitPrice * quantity - itemDiscount);
            double totalCost = item.getTotalCost() != null ? item.getTotalCost().doubleValue() : (unitCost * quantity);
            double totalProfit = item.getTotalProfit() != null ? item.getTotalProfit().doubleValue() : (subtotal - totalCost);

            return SaleItemReportResponse.builder()
                    .itemId(item.getItemId())
                    .saleId(item.getSaleId())
                    .saleNo(item.getSaleNo())
                    .saleReference(item.getSaleNo())
                    .saleDate(item.getSaleDate())
                    .saleStatus(item.getSaleStatus())
                    .paymentStatus(item.getPaymentStatus())
                    .storeId(item.getStoreId())
                    .storeName(item.getStoreName())
                    .customerId(item.getCustomerId())
                    .customerName(item.getCustomerName())
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .productCode(item.getProductCode())
                    .modelId(item.getModelId())
                    .modelName(item.getModelName())
                    .brandId(item.getBrandId())
                    .brandName(item.getBrandName())
                    .categoryId(item.getCategoryId())
                    .categoryName(item.getCategoryName())
                    .quantity(quantity)
                    .returnedQuantity(returnedQuantity)
                    .unitPrice(unitPrice)
                    .unitCost(unitCost)
                    .itemDiscount(itemDiscount)
                    .subtotal(subtotal)
                    .totalCost(totalCost)
                    .totalProfit(totalProfit)
                    .build();
        });
    }

    private SaleResponse mapToSaleResponse(ViewSalesReport v) {
        SaleResponse response = new SaleResponse();
        response.setId(v.getSaleId());
        response.setNo(v.getSaleNo());
        response.setReference(v.getReference());
        response.setDate(v.getSaleDate() != null ? v.getSaleDate().toString() : null);
        response.setStatus(v.getSaleStatus());
        response.setPaymentStatus(v.getPaymentStatus());
        response.setGrandTotal(v.getGrandTotal() != null ? v.getGrandTotal().doubleValue() : 0.0);
        response.setDiscount(v.getDiscount() != null ? v.getDiscount().doubleValue() : 0.0);
        response.setPaidAmount(v.getPaidAmount() != null ? v.getPaidAmount().doubleValue() : 0.0);
        response.setReturnAmount(v.getReturnAmount() != null ? v.getReturnAmount().doubleValue() : 0.0);
        response.setStoreId(v.getStoreId());
        response.setStoreName(v.getStoreName());
        response.setCustomerId(v.getCustomerId());
        response.setCustomerName(v.getCustomerName());
        response.setUserId(v.getUserId());
        response.setUserName(v.getCashierName());
        response.setCashRegisterId(v.getCashRegisterId());
        return response;
    }
}
