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
@Table(name = "view_bank_transactions_report")
public class ViewBankReport {

    @Id
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "bank_id")
    private Long bankId;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "bank_current_balance")
    private BigDecimal bankCurrentBalance;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "transaction_reference")
    private String transactionReference;

    @Column(name = "transaction_status")
    private String transactionStatus;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "description")
    private String description;

    @Column(name = "sale_id")
    private Long saleId;

    @Column(name = "sale_no")
    private String saleNo;

    @Column(name = "sale_reference")
    private String saleReference;

    @Column(name = "purchase_id")
    private Long purchaseId;

    @Column(name = "purchase_reference_no")
    private String purchaseReferenceNo;

    @Column(name = "expense_id")
    private Long expenseId;

    @Column(name = "expense_reference")
    private String expenseReference;

    @Column(name = "flow_type")
    private String flowType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

