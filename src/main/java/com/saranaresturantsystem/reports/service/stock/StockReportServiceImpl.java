package com.saranaresturantsystem.reports.service.stock;

import com.saranaresturantsystem.entities.catalog.Brand;
import com.saranaresturantsystem.entities.catalog.Category;
import com.saranaresturantsystem.entities.inventory.Stores;
import com.saranaresturantsystem.repository.Inventory.StoreRepsoitory;
import com.saranaresturantsystem.repository.catalog.BrandRepository;
import com.saranaresturantsystem.repository.catalog.CategoryRepository;
import com.saranaresturantsystem.reports.dto.stock.*;
import com.saranaresturantsystem.reports.entities.ViewStockMovementReport;
import com.saranaresturantsystem.reports.entities.ViewStockReport;
import com.saranaresturantsystem.reports.repository.ViewStockMovementReportRepository;
import com.saranaresturantsystem.reports.repository.ViewStockReportRepository;
import com.saranaresturantsystem.reports.specification.stock.ViewStockMovementReportSpec;
import com.saranaresturantsystem.reports.specification.stock.ViewStockReportSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockReportServiceImpl implements StockReportService {

    private final ViewStockReportRepository viewStockReportRepository;
    private final ViewStockMovementReportRepository viewStockMovementReportRepository;
    private final StoreRepsoitory storeRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    @Override
    @Transactional(readOnly = true)
    public StockReportResponse getStockReport(StockReportFilter filter) {
        String stockStatus = filter.stockStatus() != null && !filter.stockStatus().isBlank() && !"ALL".equalsIgnoreCase(filter.stockStatus().trim())
                ? filter.stockStatus().trim()
                : null;
        String search = filter.search() != null && !filter.search().isBlank() ? filter.search().trim() : null;

        // 1. High-performance summary KPIs calculated via view aggregation
        StockReportSummaryProjection summary = viewStockReportRepository.getSummary(
                filter.storeId(),
                filter.productId(),
                filter.categoryId(),
                filter.brandId(),
                stockStatus,
                filter.lowStockOnly(),
                search
        );

        BigDecimal totalQuantity = summary != null && summary.getTotalQuantity() != null ? summary.getTotalQuantity() : BigDecimal.ZERO;
        BigDecimal totalCostValue = summary != null && summary.getTotalCostValue() != null ? summary.getTotalCostValue() : BigDecimal.ZERO;
        BigDecimal totalSaleValue = summary != null && summary.getTotalSaleValue() != null ? summary.getTotalSaleValue() : BigDecimal.ZERO;
        BigDecimal totalPotentialProfit = summary != null && summary.getTotalPotentialProfit() != null ? summary.getTotalPotentialProfit() : BigDecimal.ZERO;
        Long totalItems = summary != null && summary.getTotalItems() != null ? summary.getTotalItems() : 0L;
        Long inStockCount = summary != null && summary.getInStockCount() != null ? summary.getInStockCount() : 0L;
        Long lowStockCount = summary != null && summary.getLowStockCount() != null ? summary.getLowStockCount() : 0L;
        Long outOfStockCount = summary != null && summary.getOutOfStockCount() != null ? summary.getOutOfStockCount() : 0L;

        // 2. Fetch all matching stock items
        Specification<ViewStockReport> spec = ViewStockReportSpec.filter(filter);
        List<ViewStockReport> viewStocks = viewStockReportRepository.findAll(spec);

        List<StockItemReportResponse> stockItems = viewStocks.stream()
                .map(this::mapToItemResponse)
                .toList();

        // 3. Resolve metadata (Store, Category, Brand names)
        Long storeId = filter.storeId();
        String storeName = null;
        if (storeId != null) {
            storeName = storeRepository.findById(storeId).map(Stores::getName).orElse(null);
        } else if (!viewStocks.isEmpty()) {
            Set<String> storeNames = viewStocks.stream().map(ViewStockReport::getStoreName)
                    .filter(Objects::nonNull).collect(Collectors.toSet());
            if (storeNames.size() == 1) {
                storeName = storeNames.iterator().next();
                storeId = viewStocks.get(0).getStoreId();
            } else if (storeNames.size() > 1) {
                storeName = "All Stores";
            }
        }

        Long categoryId = filter.categoryId();
        String categoryName = null;
        if (categoryId != null) {
            categoryName = categoryRepository.findById(categoryId).map(Category::getName).orElse(null);
        }

        Long brandId = filter.brandId();
        String brandName = null;
        if (brandId != null) {
            brandName = brandRepository.findById(brandId).map(Brand::getName).orElse(null);
        }

        return StockReportResponse.builder()
                .totalQuantity(totalQuantity)
                .totalCostValue(totalCostValue)
                .totalSaleValue(totalSaleValue)
                .totalPotentialProfit(totalPotentialProfit)
                .totalItems(totalItems)
                .inStockCount(inStockCount)
                .lowStockCount(lowStockCount)
                .outOfStockCount(outOfStockCount)
                .storeId(storeId)
                .storeName(storeName)
                .categoryId(categoryId)
                .categoryName(categoryName)
                .brandId(brandId)
                .brandName(brandName)
                .stocks(stockItems)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockItemReportResponse> getStockReportPaged(StockReportFilter filter, Pageable pageable) {
        Specification<ViewStockReport> spec = ViewStockReportSpec.filter(filter);
        return viewStockReportRepository.findAll(spec, pageable).map(this::mapToItemResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public StockReportSummaryProjection getStockSummary(StockReportFilter filter) {
        String stockStatus = filter.stockStatus() != null && !filter.stockStatus().isBlank() && !"ALL".equalsIgnoreCase(filter.stockStatus().trim())
                ? filter.stockStatus().trim()
                : null;
        String search = filter.search() != null && !filter.search().isBlank() ? filter.search().trim() : null;

        return viewStockReportRepository.getSummary(
                filter.storeId(),
                filter.productId(),
                filter.categoryId(),
                filter.brandId(),
                stockStatus,
                filter.lowStockOnly(),
                search
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockMovementItemResponse> getStockMovements(StockMovementReportFilter filter, Pageable pageable) {
        Specification<ViewStockMovementReport> spec = ViewStockMovementReportSpec.filter(filter);
        return viewStockMovementReportRepository.findAll(spec, pageable).map(this::mapToMovementResponse);
    }

    private StockItemReportResponse mapToItemResponse(ViewStockReport s) {
        return StockItemReportResponse.builder()
                .stockId(s.getStockId())
                .productId(s.getProductId())
                .productCode(s.getProductCode())
                .productName(s.getProductName())
                .imageUrl(s.getImageUrl())
                .productStatus(s.getProductStatus())
                .hasSerial(s.getHasSerial())
                .modelId(s.getModelId())
                .modelName(s.getModelName())
                .brandId(s.getBrandId())
                .brandName(s.getBrandName())
                .categoryId(s.getCategoryId())
                .categoryName(s.getCategoryName())
                .storeId(s.getStoreId())
                .storeName(s.getStoreName())
                .storeCode(s.getStoreCode())
                .variantValueId(s.getVariantValueId())
                .variantValueName(s.getVariantValueName())
                .currentQuantity(s.getCurrentQuantity())
                .alertQuantity(s.getAlertQuantity())
                .reorderLevel(s.getReorderLevel())
                .costPrice(s.getCostPrice())
                .salePrice(s.getSalePrice())
                .totalCostValue(s.getTotalCostValue())
                .totalSaleValue(s.getTotalSaleValue())
                .potentialProfit(s.getPotentialProfit())
                .marginPercentage(s.getMarginPercentage())
                .stockStatus(s.getStockStatus())
                .availableSerialsCount(s.getAvailableSerialsCount())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }

    private StockMovementItemResponse mapToMovementResponse(ViewStockMovementReport tx) {
        return StockMovementItemResponse.builder()
                .transactionId(tx.getTransactionId())
                .productId(tx.getProductId())
                .productCode(tx.getProductCode())
                .productName(tx.getProductName())
                .imageUrl(tx.getImageUrl())
                .modelId(tx.getModelId())
                .modelName(tx.getModelName())
                .brandId(tx.getBrandId())
                .brandName(tx.getBrandName())
                .categoryId(tx.getCategoryId())
                .categoryName(tx.getCategoryName())
                .storeId(tx.getStoreId())
                .storeName(tx.getStoreName())
                .storeCode(tx.getStoreCode())
                .quantity(tx.getQuantity())
                .transactionType(tx.getTransactionType())
                .referenceId(tx.getReferenceId())
                .transactionDate(tx.getTransactionDate())
                .notes(tx.getNotes())
                .createdAt(tx.getCreatedAt())
                .createdBy(tx.getCreatedBy())
                .build();
    }
}
