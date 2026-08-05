package com.saranaresturantsystem.entities.purchase;

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
@Entity(name = "tbl_suppliers")
@Table(name = "tbl_suppliers", indexes = {
        @Index(name = "idx_supplier_name", columnList = "name"),
        @Index(name = "idx_supplier_code", columnList = "code"),
        @Index(name = "idx_supplier_phone", columnList = "phone")
})
public class Suppliers extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    @Column(length = 50 , nullable = false , unique = true)
    private String code ;
    @Column(length = 100 , nullable = false , unique = true)
    private String name ;
    @Column(length = 50 )
    private  String phone ;
    private  String email;
    private  String address ;
    private  String city ;
    private  String  country;
    private  String note ;
    private  String status ;

}
