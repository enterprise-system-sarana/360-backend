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
@Table(name = "tbl_customer", indexes = {
        @Index(name = "idx_customer_name", columnList = "name"),
        @Index(name = "idx_customer_code", columnList = "code"),
})
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String name;
    @Column(length = 50)
    private String code;
    @Column(length = 50)
    private String phone;
    private String email;
    private String note;
    private String status;


}
