package com.saranaresturantsystem.entities.purchase;

import com.saranaresturantsystem.entities.BaseEntity;
import com.saranaresturantsystem.entities.finances.Banks;
import com.saranaresturantsystem.entities.inventory.Stores;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tbl_purchases")
public class Purchase extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_no", length = 50)
    private String referenceNo;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(precision = 25, scale = 4)
    private BigDecimal total;

    @Column(precision = 25, scale = 4)
    private BigDecimal discount;

    @Column(precision = 25, scale = 4)
    private BigDecimal grandTotal;

    @Column(length = 50)
    private String status;

    @Column(name = "paid_amount", precision = 25, scale = 4)
    private BigDecimal paidAmount;

    @Column(name = "due_amount", precision = 25, scale = 4)
    private BigDecimal dueAmount;

    @Column(name = "payment_status", length = 50)
    private String paymentStatus;

    @Column(columnDefinition = "TEXT")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id")
    private Banks banks ;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private  Suppliers suppliers ;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Stores stores ;
    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseItem> purchaseItems;
}