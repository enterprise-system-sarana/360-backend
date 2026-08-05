package com.saranaresturantsystem.entities.purchase;

import com.saranaresturantsystem.entities.BaseEntity;
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
public class Purchases extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_no", length = 50)
    private String referenceNo;

    @Column(name = "supplier_id")
    private Long supplierId;

    @Column(name = "store_id")
    private Long storeId;

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

    @Column(columnDefinition = "TEXT")
    private String note;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Purchase_Items> purchaseItems;
}