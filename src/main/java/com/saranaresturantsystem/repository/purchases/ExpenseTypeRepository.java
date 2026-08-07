package com.saranaresturantsystem.repository.purchases;

import com.saranaresturantsystem.entities.purchase.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ExpenseTypeRepository extends JpaRepository<ExpenseType, Long>, JpaSpecificationExecutor<ExpenseType> {
}