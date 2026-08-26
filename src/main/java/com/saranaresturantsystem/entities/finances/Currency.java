package com.saranaresturantsystem.entities.finances;

import com.saranaresturantsystem.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_currency")
public class Currency extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id ;
    @Column(unique = true , nullable = false , length = 50)
    private  String code ;
    @Column(unique = true , nullable = false , length = 50)
    private  String name ;
    @Column(  length = 20)
    private  String operation ;
    private  double rate ;
    @Column(length = 50)
    private  String symbol ;
    @Column(length = 50)
    private  String status ;
}
