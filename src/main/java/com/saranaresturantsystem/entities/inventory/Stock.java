package com.saranaresturantsystem.entities.inventory;



import com.saranaresturantsystem.entities.BaseEntity;
import com.saranaresturantsystem.entities.catalog.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tbl_stock", uniqueConstraints =
        {@UniqueConstraint(name = "uk_stock_product_variant_store",
                columnNames = {"product_id", "variant_value_id", "store_id"})})
public class Stock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Stores stores;

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal quantity;

    @Column(name = "alert_quantity", precision = 15, scale = 4, columnDefinition = "DECIMAL(15,4) DEFAULT 0.0000")
    private BigDecimal alertQuantity = BigDecimal.ZERO;
}