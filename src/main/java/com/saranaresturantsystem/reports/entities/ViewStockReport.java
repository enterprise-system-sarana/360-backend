package com.saranaresturantsystem.reports.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Immutable
@Table(name = "view_stock_report")
public class ViewStockReport {

    @Id
    @Column(name = "stock_id")
    private Long stockId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "product_status")
    private String productStatus;

    @Column(name = "has_serial")
    private Boolean hasSerial;

    @Column(name = "model_id")
    private Long modelId;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "brand_id")
    private Long brandId;

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "store_code")
    private String storeCode;

    @Column(name = "variant_value_id")
    private Long variantValueId;

    @Column(name = "variant_value_name")
    private String variantValueName;

    @Column(name = "current_quantity")
    private BigDecimal currentQuantity;

    @Column(name = "alert_quantity")
    private BigDecimal alertQuantity;

    @Column(name = "reorder_level")
    private Integer reorderLevel;

    @Column(name = "cost_price")
    private BigDecimal costPrice;

    @Column(name = "sale_price")
    private BigDecimal salePrice;

    @Column(name = "total_cost_value")
    private BigDecimal totalCostValue;

    @Column(name = "total_sale_value")
    private BigDecimal totalSaleValue;

    @Column(name = "potential_profit")
    private BigDecimal potentialProfit;

    @Column(name = "margin_percentage")
    private BigDecimal marginPercentage;

    @Column(name = "stock_status")
    private String stockStatus;

    @Column(name = "available_serials_count")
    private Long availableSerialsCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
