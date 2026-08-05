package com.saranaresturantsystem.entities.finances;

import com.saranaresturantsystem.entities.BaseEntity;
import com.saranaresturantsystem.enums.StatusType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "banks", indexes = {
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
    @Column(name = "opending_banlance")
    private Number openingBalance;
    @Column(name = "current_balance")
    private Number currentBalance;
    @Enumerated(EnumType.STRING)
    private StatusType status;


}
