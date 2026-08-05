package com.saranaresturantsystem.entities.catalog;

import com.saranaresturantsystem.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_model", indexes = {
        @Index(name = "idx_model_name", columnList = "name"),
        @Index(name = "idx_model_brand", columnList = "brand_id"),
        @Index(name = "idx_model_category", columnList = "category_id")
})
public class Model extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50, nullable = false, unique = true)
    private String name;
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", referencedColumnName = "id")
    @JsonIgnore
    private Brand brand;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @JsonIgnore
    private Category category;
    @Column(length = 50)
    private String status;

    @OneToMany(mappedBy = "models")
    @JsonIgnore
    private List<Product> products;

}
