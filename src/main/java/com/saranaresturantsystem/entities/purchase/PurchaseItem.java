package com.saranaresturantsystem.entities.purchase;


import com.saranaresturantsystem.entities.catalog.Product;
import com.saranaresturantsystem.entities.catalog.ProductSerials;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tbl_purchase_items")
public class PurchaseItem  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(precision = 15, scale = 4, nullable = false)
    private BigDecimal quantity;

    @Column(precision = 25, scale = 4, nullable = false)
    private BigDecimal cost;
    private  BigDecimal price ;
    @Column(precision = 25, scale = 4)
    private BigDecimal subtotal;

    @OneToMany(mappedBy = "purchaseItem", fetch = FetchType.LAZY)
    private List<ProductSerials> productSerials;
}