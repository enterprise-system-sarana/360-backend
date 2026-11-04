package com.saranaresturantsystem.repository.sales;

import com.saranaresturantsystem.entities.sales.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
    Optional<Payment> findByPaymentNo(String paymentNo);

    @Query("SELECT p FROM Payment p WHERE p.sales.id = :saleId")
    List<Payment> findBySalesId(@Param("saleId") Long saleId);

    @Query("SELECT p FROM Payment p WHERE p.purchase.id = :purchaseId")
    List<Payment> findByPurchaseId(@Param("purchaseId") Long purchaseId);
}
