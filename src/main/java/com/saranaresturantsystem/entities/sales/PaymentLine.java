package com.saranaresturantsystem.entities.sales;

import com.saranaresturantsystem.entities.BaseEntity;
import com.saranaresturantsystem.entities.finances.Banks;
import com.saranaresturantsystem.entities.finances.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "tbl_payment_line",
        indexes = {
                @Index(name = "idx_payment_lines_payment_id", columnList = "payment_id"),
                @Index(name = "idx_payment_lines_bank_id", columnList = "bank_id"),
                @Index(name = "idx_payment_lines_transaction_no", columnList = "transaction_no")
        }
)
public class PaymentLine  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "payment_method", length = 50, nullable = false)
    private String paymentMethod;

    @Column(name = "amount", nullable = false, precision = 38, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_no", length = 100)
    private String transactionNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", nullable = true)
    private Banks bank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = true)
    private Currency currency;
}