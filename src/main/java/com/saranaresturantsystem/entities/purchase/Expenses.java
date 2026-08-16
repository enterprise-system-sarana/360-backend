package com.saranaresturantsystem.entities.purchase;

import com.saranaresturantsystem.entities.BaseEntity;
import com.saranaresturantsystem.entities.finances.Banks;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_expenses", indexes = {
        @Index(name = "idx_expenses_store", columnList = "store_id"),
        @Index(name = "idx_expenses_bank", columnList = "bank_id"),
        @Index(name = "idx_expenses_type", columnList = "expense_type_id")
})
public class Expenses extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference", length = 50, nullable = false)
    private String reference;

    @Column(name = "amount", precision = 25, scale = 4, nullable = false)
    private BigDecimal amount;

    @Column(name = "note", length = 1000)
    private String note;

    @Column(name = "store_id", nullable = false)
    private Integer storeId;

    @Column(name = "description", length = 255)
    private String description;

    @Column(length = 50)
    private String status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id")
    private Banks bank;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_type_id")
    private ExpenseType expenseType;
}