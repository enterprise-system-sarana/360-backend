package com.saranaresturantsystem.entities.quote;

import com.saranaresturantsystem.entities.customer.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_quote")
public class Quote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quote_id")
    private Long id;

    private LocalDateTime date;

    @Column(name = "reference", length = 100, unique = true, nullable = false)
    private String reference;

    @Column(name = "no", length = 100, unique = true, nullable = false)
    private String no;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private BigDecimal grandTotal;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(length = 50)
    private String status;

    @Column(length = 50)
    private String statusPayment;

    private BigDecimal paidAmount;
    private BigDecimal returnAmount;
    private String noted;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuoteItem> quoteItems;
}