package com.saranaresturantsystem.entities.inventory;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_store")
public class Stores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "st_name", length = 50, nullable = false)
    private String name;

    @Column(name = "st_code", length = 20, nullable = false)
    private String code;

    @Column(length = 500 ,name = "st_logo")
    private String logo;

    @Column(length = 100  , name = "st_email")
    private String email;

    @Column(length = 25, nullable = false , name = "st_phone")
    private String phone;

    @Column( name = "st_address1",length = 500)
    private String address1;

    @Column(length = 500 ,name = "st_address2")
    private String address2;

    @Column(length = 50 ,name = "st_city")
    private String city;

    @Column(length = 50 , name = "st_state")
    private String state;

    @Column(name = "st_postal_code", length = 50)
    private String postalCode;

    @Column(length = 50 , name = "st_country")
    private String country;

    @Column(name = "currency_code", length = 50)
    private String currencyCode;

    @Column(name = "st_receipt_header", columnDefinition = "TEXT")
    private String receiptHeader;

    @Column(name = "st_receipt_footer", columnDefinition = "TEXT")
    private String receiptFooter;

    private  String status ;
}
