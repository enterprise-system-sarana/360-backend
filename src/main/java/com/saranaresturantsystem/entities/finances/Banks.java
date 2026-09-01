package com.saranaresturantsystem.entities.finances;

import com.saranaresturantsystem.entities.BaseEntity;
import com.saranaresturantsystem.entities.purchase.Expenses;
import com.saranaresturantsystem.entities.sales.Payment;
import com.saranaresturantsystem.entities.sales.Sales;
import jakarta.persistence.*;
import lombok.*;
import com.saranaresturantsystem.entities.purchase.Purchase;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_bank", indexes = {
        @Index(name = "idx_bank_name", columnList = "name"),
        @Index(name = "idx_bank_account_name", columnList = "account_name"),
        @Index(name = "idx_bank_account_number", columnList = "account_number")
})
public class Banks extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "account_name",length = 50 , unique = true , nullable = false)
    private String accountName;
    @Column(name = "account_number",length = 50 , unique = true , nullable = false)
    private String accountNumber;
    @Column(name = "opening_balance")
    private BigDecimal openingBalance;
    @Column(name = "current_balance")
    private BigDecimal currentBalance;
    @Column(length = 50)
    private String status;

    @OneToMany(mappedBy = "bank")
    private List<Expenses> expenses;

    @OneToMany(mappedBy = "banks")
    private List<Payment> banks;

    @OneToMany(mappedBy = "banks")
    private List<Purchase> purchases;

    @OneToMany(mappedBy = "banks")
    private  List<Sales> sales ;
}
