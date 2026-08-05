package com.saranaresturantsystem.entities.catalog;

import com.saranaresturantsystem.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "tbl_variant_values")
@Table(name = "tbl_variant_values", indexes = {
        @Index(name = "idx_variant_value_name", columnList = "name"),
        @Index(name = "idx_variant_value_code", columnList = "code"),
        @Index(name = "idx_variant_value_type", columnList = "variant_type_id")
})
public class VariantValue extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "value_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String code;

    @Column(length = 50, nullable = false)
    private String name;
    @Column(name = "variant_type_id", nullable = false)
    private Long variantTypeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_type_id", insertable = false, updatable = false)
    @JsonIgnore
    private VariantType variantType;

    private String status;
}