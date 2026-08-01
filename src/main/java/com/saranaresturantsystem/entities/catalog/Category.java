package com.saranaresturantsystem.entities.catalog;


import com.saranaresturantsystem.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_category")
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id ;
    @Column(length = 50 , nullable = false  , unique = true)
    private  String name ;
    @Column(length = 50 , nullable = false , unique = true)
    private  String  code ;
    private  String imageUrl ;
    @Column(length = 50 , nullable = false)
    private  String status ;

    @OneToMany(mappedBy = "category")
    private List<Product> products;
}
