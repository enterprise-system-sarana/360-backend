package com.saranaresturantsystem.reports.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
@Immutable
@Table(name = "view_purchase_report")
public class ViewPurchaseReport {

    @Id
    @Column(name = "purchase_id")
    private Long purchaseId;

    @Column(name = "reference_no")
    private String referenceNo;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "status")
    private String status;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "total")
    private BigDecimal total;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "grand_total")
    private BigDecimal grandTotal;

    @Column(name = "paid_amount")
    private BigDecimal paidAmount;

    @Column(name = "due_amount")
    private BigDecimal dueAmount;

    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "supplier_id")
    private Long supplierId;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "bank_id")
    private Long bankId;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "total_items_count")
    private Long totalItemsCount;

    @Column(name = "total_quantity")
    private BigDecimal totalQuantity;
}
