package com.saranaresturantsystem.entities.sales;

import com.saranaresturantsystem.entities.BaseEntity;
import com.saranaresturantsystem.entities.inventory.Stores;
import com.saranaresturantsystem.entities.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "tbl_cash_register", indexes = {
        @Index(name = "idx_cash_register_store_id", columnList = "store_id"),
        @Index(name = "idx_cash_register_user_id", columnList = "user_id"),
        @Index(name = "idx_cash_register_status", columnList = "status"),
        @Index(name = "idx_cash_register_opened_at", columnList = "opened_at")
})
public class CashRegister extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "register_no", length = 100, nullable = false, unique = true)
    private String registerNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Stores store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "opening_balance", nullable = false, precision = 25, scale = 4)
    private BigDecimal openingBalance;

    @Column(name = "closing_balance", precision = 25, scale = 4)
    private BigDecimal closingBalance;

    @Column(name = "expected_balance", precision = 25, scale = 4)
    private BigDecimal expectedBalance;

    @Column(name = "total_sales", precision = 25, scale = 4)
    private BigDecimal totalSales;

    @Column(name = "total_cash_sales", precision = 25, scale = 4)
    private BigDecimal totalCashSales;

    @Column(name = "total_bank_sales", precision = 25, scale = 4)
    private BigDecimal totalBankSales;

    @Column(name = "total_returns", precision = 25, scale = 4)
    private BigDecimal totalReturns;

    @Column(name = "total_cash_in", precision = 25, scale = 4)
    private BigDecimal totalCashIn;

    @Column(name = "total_cash_out", precision = 25, scale = 4)
    private BigDecimal totalCashOut;

    @Column(name = "difference", precision = 25, scale = 4)
    private BigDecimal difference;

    @Column(name = "sale_count")
    private Integer saleCount;

    @Column(name = "status", length = 50, nullable = false)
    private String status;

    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "closing_note", columnDefinition = "TEXT")
    private String closingNote;
}
