package com.saranaresturantsystem.repository.sales;

import com.saranaresturantsystem.entities.sales.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

    @Query("""
            SELECT MAX(p.id)
            FROM Payment p
         """)
    Long findMaxId();
    List<Payment> findBySalesIdAndStatus(Long saleId, String status);

    Optional<Payment> findByPaymentNo(String paymentNo);

    boolean existsByPaymentNo(String paymentNo);
}