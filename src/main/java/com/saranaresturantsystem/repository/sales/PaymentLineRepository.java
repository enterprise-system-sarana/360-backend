package com.saranaresturantsystem.repository.sales;

import com.saranaresturantsystem.entities.sales.PaymentLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentLineRepository extends JpaRepository<PaymentLine, Long> {
    List<PaymentLine> findByPaymentId(Long paymentId);
}