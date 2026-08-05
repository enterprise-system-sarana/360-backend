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
@Entity(name = "tbl_brands")
@Table(name = "tbl_brands", indexes = {
        @Index(name = "idx_brand_name", columnList = "name")
})
public class Brand extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50, nullable = false, unique = true)
    private String name;
    @Column(length = 500, nullable = false)
    private String imageUrl;
    @Column(length = 50, nullable = false)
    private String status;

    @OneToMany(mappedBy = "brand")
    @JsonIgnore
    private List<Model> models;

}
