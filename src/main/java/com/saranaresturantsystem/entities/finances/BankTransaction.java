package com.saranaresturantsystem.entities.finances;

import com.saranaresturantsystem.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_bank_transactions")
public class BankTransaction  extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "expense_id")
    private Long expenseId;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    @Column(name = "transaction_reference", nullable = false, unique = true)
    private String transactionReference;
    @Column(name = "transaction_type", nullable = false)
    private String transactionType;
    @Column(nullable = false)
    private String status;
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
    @Column(columnDefinition = "TEXT")
    private String description;


}