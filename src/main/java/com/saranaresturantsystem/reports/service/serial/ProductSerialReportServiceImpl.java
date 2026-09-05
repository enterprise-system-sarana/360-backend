package com.saranaresturantsystem.reports.service.serial;

import com.saranaresturantsystem.entities.catalog.ProductSerials;
import com.saranaresturantsystem.repository.catalog.ProductSerialsRepository; // ផ្អែកលើ package របស់ Repository ក្នុងប្រព័ន្ធអ្នក
import com.saranaresturantsystem.reports.dto.serial.ProductSerialReportFilter;
import com.saranaresturantsystem.reports.dto.serial.ProductSerialReportResponse;
import com.saranaresturantsystem.reports.specification.serial.ProductSerialReportSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductSerialReportServiceImpl implements ProductSerialReportService {

    private final ProductSerialsRepository productSerialsRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductSerialReportResponse> getProductSerialsReport(ProductSerialReportFilter filter, Pageable pageable) {
        Specification<ProductSerials> spec = ProductSerialReportSpec.filter(filter);

        return productSerialsRepository.findAll(spec, pageable).map(serial ->
                ProductSerialReportResponse.builder()
                        .id(serial.getId())
                        .productId(serial.getProduct().getId())
                        .productName(serial.getProduct().getName())
                        .storeId(serial.getStores().getId())
                        .storeName(serial.getStores().getName())
                        .barcode(serial.getBarcode())
                        .price(serial.getPrice())
                        .cost(serial.getCost())
                        .quantity(serial.getQuantity())
                        // ផ្អែកលើ Entity របស់អ្នក tbl_product_serials មិនមាន alertQuantity ទេ ដូច្នេះយើងអាចលុបចោល ឬកំណត់ជា null
                        .alertQuantity(null)
                        .purchaseId(serial.getPurchaseId())
                        .status(serial.getStatus())
                        .createdAt(serial.getCreatedAt())
                        .build()
        );
    }
}