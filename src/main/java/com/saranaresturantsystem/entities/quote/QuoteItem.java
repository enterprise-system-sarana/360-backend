package com.saranaresturantsystem.entities.quote;

import com.saranaresturantsystem.entities.catalog.Product;
import com.saranaresturantsystem.entities.catalog.ProductSerials;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_quote_items")
public class QuoteItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id")
    private Quote quote;

    @Column(name = "unit_quantity")
    private BigDecimal qty;

    @Column(name = "price")
    private BigDecimal price;

    private BigDecimal discount_item;
    private BigDecimal subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_serials_id", nullable = false)
    private ProductSerials product_serials;
}