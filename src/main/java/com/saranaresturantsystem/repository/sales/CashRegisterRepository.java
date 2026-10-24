package com.saranaresturantsystem.repository.sales;

import com.saranaresturantsystem.entities.sales.CashRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CashRegisterRepository extends JpaRepository<CashRegister, Long>, JpaSpecificationExecutor<CashRegister> {

    @Query("SELECT cr FROM CashRegister cr WHERE cr.user.id = :userId AND cr.status = 'OPEN' AND cr.deletedAt IS NULL")
    Optional<CashRegister> findOpenRegisterByUserId(@Param("userId") Long userId);

    @Query("SELECT cr FROM CashRegister cr WHERE cr.store.id = :storeId AND cr.status = 'OPEN' AND cr.deletedAt IS NULL")
    Optional<CashRegister> findOpenRegisterByStoreId(@Param("storeId") Long storeId);

    @Query("SELECT CASE WHEN COUNT(cr) > 0 THEN TRUE ELSE FALSE END FROM CashRegister cr WHERE cr.user.id = :userId AND cr.status = 'OPEN' AND cr.deletedAt IS NULL")
    boolean existsOpenRegisterByUserId(@Param("userId") Long userId);
}
