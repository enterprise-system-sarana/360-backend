package com.saranaresturantsystem.repository.finances;

import com.saranaresturantsystem.entities.finances.Bank_Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BackTransactionRepository extends JpaRepository<Bank_Transactions, Long>, JpaSpecificationExecutor<Bank_Transactions> {
}
