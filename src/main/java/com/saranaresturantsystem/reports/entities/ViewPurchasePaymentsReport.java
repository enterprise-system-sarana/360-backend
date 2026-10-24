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
@Table(name = "view_purchase_payments_report")
public class ViewPurchasePaymentsReport {

    @Id
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "payment_no")
    private String paymentNo;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "received_amount")
    private BigDecimal receivedAmount;

    @Column(name = "return_amount")
    private BigDecimal returnAmount;

    @Column(name = "due_amount")
    private BigDecimal dueAmount;

    @Column(name = "note")
    private String note;

    @Column(name = "purchase_id")
    private Long purchaseId;

    @Column(name = "purchase_reference_no")
    private String purchaseReferenceNo;

    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "supplier_id")
    private Long supplierId;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created_by_user")
    private String createdByUser;

    @Column(name = "payment_methods")
    private String paymentMethods;

    @Column(name = "bank_names")
    private String bankNames;

    @Column(name = "total_lines_count")
    private Long totalLinesCount;
}
