package com.saranaresturantsystem.repository.finances;

import com.saranaresturantsystem.entities.finances.Banks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BackRepository extends JpaRepository<Banks , Long> , JpaSpecificationExecutor<Banks> {
    boolean existsByAccountNameIgnoreCaseAndIdNot(String accountName, Long id);
    boolean existsByAccountNumberAndIdNot(String accountNumber, Long id);
}
