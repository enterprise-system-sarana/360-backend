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
@Table(name = "view_sale_payments_report")
public class ViewSalePaymentsReport {

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

    @Column(name = "sale_id")
    private Long saleId;

    @Column(name = "sale_no")
    private String saleNo;

    @Column(name = "sale_reference")
    private String saleReference;

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

    @Column(name = "payment_methods")
    private String paymentMethods;

    @Column(name = "bank_names")
    private String bankNames;

    @Column(name = "total_lines_count")
    private Long totalLinesCount;
}
