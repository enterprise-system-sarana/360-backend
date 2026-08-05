package com.saranaresturantsystem.repository.Inventory;

import com.saranaresturantsystem.entities.inventory.Inventory_Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryTransactionsRepository extends JpaRepository<Inventory_Transactions, Long>, JpaSpecificationExecutor<Inventory_Transactions> {
}
