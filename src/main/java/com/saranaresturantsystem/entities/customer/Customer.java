package com.saranaresturantsystem.entities.customer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "tbl_customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50,nullable = false,unique = true)
    private String name;
    @Column(length = 50,nullable = false,unique = true)
    private String code;
    @Column(length = 50,nullable = false,unique = true)
    private String phone;
    private String email;
    private String note;
    private String status;


}
