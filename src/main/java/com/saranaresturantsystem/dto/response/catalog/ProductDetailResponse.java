package com.saranaresturantsystem.dto.response.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDetailResponse {
    private Long id;
    private String name;
    private Long brandId;
    private String brandName;
    private Long categoryId;
    private String categoryName;
    private Long modelId;
    private String modelName;
    private Integer reorderLevel;
    private BigDecimal quantity;
    private Long availableSerials;
    private List<SerialInfo> serials;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SerialInfo {
        private Long id;
        private String serialNumber;
        private String barcode;
        private BigDecimal costPrice;
        private BigDecimal sellingPrice;
        private String status;
    }
}
