package com.saranaresturantsystem.entities.catalog;

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
@Table(name = "tbl_product_serials", indexes = {
        @Index(name = "idx_product", columnList = "product_id"),
})
public class ProductSerials extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "barcode", length = 100, unique = true)
    private String barcode;

    @Column(nullable = false, precision = 25, scale = 4)
    private BigDecimal price;

    @Column(precision = 25, scale = 4)
    private BigDecimal cost;

    @Column(precision = 15, scale = 4, columnDefinition = "DECIMAL(15,4) DEFAULT 0.0000")
    private BigDecimal quantity = new BigDecimal("0.0000");

    @Column(name = "alert_quantity", precision = 10, scale = 4, columnDefinition = "DECIMAL(10,4) DEFAULT 0.0000")
    private BigDecimal alertQuantity = new BigDecimal("0.0000");

    @Column(columnDefinition = "SMALLINT DEFAULT 0")
    private Integer deleted = 0;

    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "purchase_id")
    private Long purchaseId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Product product;
    @Column(length = 50 , nullable = false)
    private  String status ;
}