//package com.saranaresturantsystem.entities.catalog;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//@AllArgsConstructor
//@Getter
//@Setter
//@NoArgsConstructor
//@Entity
//@Table(name = "tbl_product_variant", indexes = {
//        @Index(name = "idx_product_variant_code", columnList = "code"),
//        @Index(name = "idx_product_variant_product", columnList = "product_id")
//})
//public class ProductVariant {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "variant_id")
//    private Long id;
//    @Column(length = 50, unique = true, nullable = false)
//    private String code;
//    private BigDecimal costPrice;
//    private BigDecimal sellingPrice;
//    private String imageUrl;
//    @Column(length = 50)
//    private String status;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id", referencedColumnName = "id")
//    @JsonIgnore
//    private Product product;
//
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "tbl_product_variant_value", joinColumns = @JoinColumn(name = "variant_id"), inverseJoinColumns = @JoinColumn(name = "value_id"))
//    @JsonIgnore
//    private List<VariantValue> variantValues;
//
//}
