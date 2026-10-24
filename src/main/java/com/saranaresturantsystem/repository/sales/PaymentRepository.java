package com.saranaresturantsystem.repository.sales;

import com.saranaresturantsystem.entities.sales.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
    Optional<Payment> findByPaymentNo(String paymentNo);
    List<Payment> findBySalesId(Long saleId);
    List<Payment> findByPurchaseId(Long purchaseId);
}