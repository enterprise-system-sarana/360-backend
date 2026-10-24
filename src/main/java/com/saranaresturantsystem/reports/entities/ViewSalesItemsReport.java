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
@Table(name = "view_sales_items_report")
public class ViewSalesItemsReport {

    @Id
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "sale_id")
    private Long saleId;

    @Column(name = "sale_no")
    private String saleNo;

    @Column(name = "sale_date")
    private LocalDateTime saleDate;

    @Column(name = "sale_status")
    private String saleStatus;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_code")
    private String productCode;

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

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "returned_quantity")
    private BigDecimal returnedQuantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "unit_cost")
    private BigDecimal unitCost;

    @Column(name = "item_discount")
    private BigDecimal itemDiscount;

    @Column(name = "sub_total")
    private BigDecimal subTotal;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @Column(name = "total_profit")
    private BigDecimal totalProfit;
}
