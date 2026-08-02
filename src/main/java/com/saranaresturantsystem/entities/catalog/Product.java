package com.saranaresturantsystem.entities.catalog;

import com.saranaresturantsystem.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tbl_product")
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id ;
    @Column(length = 50)
    private  String code ;
    private  String noted ;
    private  String imageUrl ;
    @Column(length = 50)
    private  String status ;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id" , referencedColumnName = "id")
    private  Model models;

}
