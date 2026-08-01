package com.saranaresturantsystem.repository.Inventory;

import com.saranaresturantsystem.entities.Stores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepsoitory extends JpaRepository<Stores, Long> , JpaSpecificationExecutor<Stores> {
}
