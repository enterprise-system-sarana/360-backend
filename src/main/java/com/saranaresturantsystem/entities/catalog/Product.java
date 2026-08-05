package com.saranaresturantsystem.entities.catalog;

import com.saranaresturantsystem.entities.BaseEntity;
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
    @Column(length = 50)
    private String status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", referencedColumnName = "id")
    @JsonIgnore
    private Model models;
    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<ProductVariant> productVariantList;
}
