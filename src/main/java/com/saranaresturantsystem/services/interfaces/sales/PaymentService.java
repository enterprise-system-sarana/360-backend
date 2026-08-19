package com.saranaresturantsystem.services.interfaces.sales;

import com.saranaresturantsystem.dto.request.sales.PaymentRequest;
import com.saranaresturantsystem.dto.response.sales.PaymentResponse;
import com.saranaresturantsystem.entities.sales.Payment;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface PaymentService {
    Page<PaymentResponse> findAll(Map<String , String> params);
    PaymentResponse create(PaymentRequest request);
    PaymentResponse update(PaymentRequest request , Long id );
    PaymentResponse getById(Long id);
    Payment findById(Long id);
    void delete (Long id);

}
