package com.saranaresturantsystem.repository.sales;

import com.saranaresturantsystem.entities.sales.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Sales,Long>, JpaSpecificationExecutor<Sales> {

}
