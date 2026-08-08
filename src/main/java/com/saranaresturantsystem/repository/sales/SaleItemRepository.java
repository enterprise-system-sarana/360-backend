package com.saranaresturantsystem.repository.sales;

import com.saranaresturantsystem.entities.sales.SaleItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItems, Long> {
}
