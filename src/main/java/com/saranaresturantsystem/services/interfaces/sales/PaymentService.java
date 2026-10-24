package com.saranaresturantsystem.services.interfaces.sales;


import com.saranaresturantsystem.dto.request.sales.payment.PaymentRequestDTO;
import com.saranaresturantsystem.dto.response.payment.PaymentResponseDTO;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface PaymentService {
    PaymentResponseDTO processPayment(PaymentRequestDTO request);
    PaymentResponseDTO getPaymentById(Long id);
    Page<PaymentResponseDTO> findAll(Map<String, String> params);
}
