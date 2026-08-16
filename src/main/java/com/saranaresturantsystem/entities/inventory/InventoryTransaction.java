package com.saranaresturantsystem.entities.inventory;

import com.saranaresturantsystem.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tbl_inventory_transactions", indexes = {
        @Index(name = "idx_inv_product", columnList = "product_id"),
        @Index(name = "idx_inv_store", columnList = "store_id")
})
public class InventoryTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_variant_id")
    private Long productVariantId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(precision = 15, scale = 4, nullable = false)
    private BigDecimal quantity;

    @Column(length = 50, nullable = false)
    private String type;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(columnDefinition = "TEXT")
    private String notes;
}