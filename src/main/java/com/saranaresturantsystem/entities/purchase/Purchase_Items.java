package com.saranaresturantsystem.entities.purchase;

import com.saranaresturantsystem.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tbl_purchase_items")
public class Purchase_Items extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchases purchase;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(precision = 15, scale = 4, nullable = false)
    private BigDecimal quantity;

    @Column(precision = 25, scale = 4, nullable = false)
    private BigDecimal cost;

    @Column(precision = 25, scale = 4)
    private BigDecimal subtotal;
}