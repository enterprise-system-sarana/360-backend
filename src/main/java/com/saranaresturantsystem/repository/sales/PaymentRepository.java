package com.saranaresturantsystem.repository.sales;

import com.saranaresturantsystem.entities.sales.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

    @Query("""
            SELECT MAX(p.id)
            FROM Payment p
         """)
    Long findMaxId();
    @Query("""
            SELECT p
            FROM Payment p
            WHERE p.sales.id = :saleId
              AND p.status NOT IN ('DEL', 'INT')
         """)
    List<Payment> findActiveBySalesId(@Param("saleId") Long saleId);

    @Query("""
            SELECT p
            FROM Payment p
            WHERE p.purchase.id = :purchaseId
              AND p.status NOT IN ('DEL', 'INT')
         """)
    List<Payment> findActiveByPurchaseId(@Param("purchaseId") Long purchaseId);

    Optional<Payment> findByPaymentNo(String paymentNo);

    boolean existsByPaymentNo(String paymentNo);
}