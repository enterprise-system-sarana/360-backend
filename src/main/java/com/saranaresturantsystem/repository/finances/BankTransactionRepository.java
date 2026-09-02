package com.saranaresturantsystem.repository.finances;

import com.saranaresturantsystem.entities.finances.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long>, JpaSpecificationExecutor<BankTransaction> {
}
