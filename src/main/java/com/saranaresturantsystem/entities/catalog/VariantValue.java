package com.saranaresturantsystem.entities.catalog;

import com.saranaresturantsystem.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "tbl_variant_values")
public class VariantValue extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    private String code;

    @Column(length = 50, nullable = false, unique = true)
    private String name;
    @Column(name = "variant_type_id", nullable = false)
    private Long variantTypeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_type_id", insertable = false, updatable = false)
    private VariantType variantType;

    private String status;
}