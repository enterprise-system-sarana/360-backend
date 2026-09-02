package com.saranaresturantsystem.entities.sales;

import com.saranaresturantsystem.entities.BaseEntity;
import com.saranaresturantsystem.entities.finances.Banks;
import com.saranaresturantsystem.entities.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "tbl_payment",
        indexes = {
                @Index(name = "idx_payments_sale_id", columnList = "sale_id"),
                @Index(name = "idx_payments_payment_date", columnList = "payment_date"),
                @Index(name = "idx_payments_transaction_no", columnList = "transaction_no"),
                @Index(name = "idx_payments_bank_id", columnList = "bank_id")
        }
)
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    @Column(length = 100  )
    private  String paymentNo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id" )
    private Sales sales ;
    private  String paymentMethod ;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id")
    private Banks banks ;
    private BigDecimal amount ;
    private  String transactionNo ;
    @Column(length = 50)
    private  String status  ;
    private LocalDateTime paymentDate ;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
