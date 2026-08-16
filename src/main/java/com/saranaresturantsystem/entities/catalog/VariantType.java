package com.saranaresturantsystem.entities.catalog;

import com.saranaresturantsystem.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "tbl_variant_types")
@Table(name = "tbl_variant_types", indexes = {
        @Index(name = "idx_variant_type_name", columnList = "name"),
        @Index(name = "idx_variant_type_code", columnList = "code")
})
public class VariantType extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variant_type_id")
    private Long id;
    private String code;
    @Column(length = 50, nullable = false, unique = true)
    private String name;
    @Column(length = 50)
    private String status;
    @OneToMany(mappedBy = "variantType")
    private List<VariantValue> variantValues;
}
