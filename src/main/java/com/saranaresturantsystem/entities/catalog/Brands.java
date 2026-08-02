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
@Entity(name = "tbl_brands")
public class Brands extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    @Column(length = 50 , nullable = false , unique = true)
    private String name;
    @Column(length = 500 , nullable = false)
    private String imageUrl;
    private String status;

}
