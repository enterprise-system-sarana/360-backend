package com.saranaresturantsystem.entities.sales;

import com.saranaresturantsystem.entities.BaseEntity;

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
    private Integer no;
    @Column(name = "store_id")
    private Long storeId;
    @Column(length = 100, name = "customer_id")
    private Long customerId;
    @Column(name = "grand_total")
    private Double grandTotal;
    private Double discount;
    @Column(name = "sale_status" , length = 50)
    private String  saleStatus;
    @Column(name = "payment_status" , length = 50)
    private String paymentStatus;
    @Column(name = "paid_amount")
    private Double paidAmount;
    @Column(name = "return_amount")
    private Double returnAmount;
    private String noted;

    @OneToMany(mappedBy = "sales", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<SaleItems> items;

    @OneToMany(mappedBy = "sales")
    private  List<Payment> paymentList;


}
