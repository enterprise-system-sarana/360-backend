package com.saranaresturantsystem.entities.sales;

import com.saranaresturantsystem.entities.BaseEntity;
import com.saranaresturantsystem.enums.PaymentStatus;
import com.saranaresturantsystem.enums.SaleStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Data
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
    @Enumerated(EnumType.STRING)
    @Column(name = "sale_status")
    private SaleStatus  saleStatus;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;
    @Column(name = "paid_amount")
    private Double paidAmount;
    @Column(name = "return_amount")
    private Double returnAmount;
    private String noted;
    @Column(name = "delete_flag")
    private Integer deleteFlag = 0;
    @OneToMany(mappedBy = "sales", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<SaleItems> items;


}
