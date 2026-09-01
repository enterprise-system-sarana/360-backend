package com.saranaresturantsystem.entities.sales;

import com.saranaresturantsystem.entities.BaseEntity;

import com.saranaresturantsystem.entities.customer.Customer;
import com.saranaresturantsystem.entities.finances.Banks;
import com.saranaresturantsystem.entities.inventory.Stores;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter
@Setter
@Table(name = "tbl_sales")
public class Sales extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime date;
    private String reference;
    private String no;
    @Column(name = "grand_total")
    private Double grandTotal;
    private Double discount;
    @Column(name = "sale_status" , length = 50)
    private String  saleStatus;
    @Column(name = "payment_status" , length = 50)
    private String paymentStatus;
    @Column(name = "paid_amount")
    private Double paidAmount;
    @Column(name = "returnAmount")
    private Double returnAmount;
    private String noted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Stores store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "sales", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<SaleItems> items;

    @OneToMany(mappedBy = "sales")
    private  List<Payment> paymentList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id")
    private Banks banks ;


}
