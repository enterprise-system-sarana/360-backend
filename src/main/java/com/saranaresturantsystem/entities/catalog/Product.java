package com.saranaresturantsystem.entities.catalog;

import com.saranaresturantsystem.entities.BaseEntity;
import com.saranaresturantsystem.entities.inventory.Stock;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tbl_product", indexes = {
        @Index(name = "idx_product_code", columnList = "code"),
        @Index(name = "idx_product_model", columnList = "model_id")
})
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String code;
    private String noted;
    private String imageUrl;

    private Integer reorderLevel ;
    @Column(length = 50)
    private String status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", referencedColumnName = "id")
    private Model models;

    @OneToMany(mappedBy = "product")
    private List<Stock> stocks;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tbl_product_variant_values",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "variant_value_id", referencedColumnName = "value_id")
    )
    private List<VariantValue> variantValues;
}
