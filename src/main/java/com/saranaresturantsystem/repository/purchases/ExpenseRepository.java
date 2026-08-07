package com.saranaresturantsystem.repository.purchases;

import com.saranaresturantsystem.entities.purchase.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ExpenseRepository extends JpaRepository<Expenses, Long>, JpaSpecificationExecutor<Expenses> {
}