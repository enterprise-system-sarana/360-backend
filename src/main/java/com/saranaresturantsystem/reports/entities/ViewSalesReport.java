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
@Table(name = "view_sales_report")
public class ViewSalesReport {

    @Id
    @Column(name = "sale_id")
    private Long saleId;

    @Column(name = "sale_no")
    private String saleNo;

    @Column(name = "reference")
    private String reference;

    @Column(name = "sale_date")
    private LocalDateTime saleDate;

    @Column(name = "sale_status")
    private String saleStatus;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "grand_total")
    private BigDecimal grandTotal;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "paid_amount")
    private BigDecimal paidAmount;

    @Column(name = "return_amount")
    private BigDecimal returnAmount;

    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "cashier_name")
    private String cashierName;

    @Column(name = "cash_register_id")
    private Long cashRegisterId;

    @Column(name = "total_items_count")
    private Long totalItemsCount;

    @Column(name = "total_quantity")
    private BigDecimal totalQuantity;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @Column(name = "total_profit")
    private BigDecimal totalProfit;
}
