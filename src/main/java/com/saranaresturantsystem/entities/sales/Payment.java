package com.saranaresturantsystem.entities.sales;

import com.saranaresturantsystem.entities.BaseEntity;
import com.saranaresturantsystem.entities.purchase.Purchase;
import com.saranaresturantsystem.entities.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "tbl_payment",
        indexes = {
                @Index(name = "idx_payments_sale_id", columnList = "sale_id"),
                @Index(name = "idx_payments_purchase_id", columnList = "purchase_id"),
                @Index(name = "idx_payments_payment_date", columnList = "payment_date"),
                @Index(name = "idx_payments_status", columnList = "status"),
                @Index(name = "idx_payments_idempotency_key", columnList = "idempotency_key")
        }
)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_no", length = 100, unique = true)
    private String paymentNo;

    @Column(name = "idempotency_key", length = 150, unique = true)
    private String idempotencyKey;

    @Column(name = "total_amount", nullable = false, precision = 38, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "received_amount", precision = 38, scale = 2)
    private BigDecimal receivedAmount;

    @Column(name = "return_amount", precision = 38, scale = 2)
    private BigDecimal returnAmount;

    @Column(name = "due_amount", precision = 38, scale = 2)
    private BigDecimal dueAmount;

    @Column(length = 50, nullable = false)
    private String status;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(length = 500)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = true)
    private Sales sales;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = true)
    private Purchase purchase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PaymentLine> paymentLines = new ArrayList<>();

    public void addPaymentLine(PaymentLine line) {
        paymentLines.add(line);
        line.setPayment(this);
    }
}