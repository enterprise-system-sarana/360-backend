package com.saranaresturantsystem.services.interfaces.sales;

import com.saranaresturantsystem.dto.request.sales.CloseCashRegisterRequest;
import com.saranaresturantsystem.dto.request.sales.OpenCashRegisterRequest;
import com.saranaresturantsystem.dto.response.sales.CashRegisterResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface CashRegisterService {

    CashRegisterResponse openRegister(OpenCashRegisterRequest request, String createdBy);

    CashRegisterResponse closeRegister(Long id, CloseCashRegisterRequest request, String updatedBy);

    CashRegisterResponse getById(Long id);

    CashRegisterResponse getCurrentOpen();

    Page<CashRegisterResponse> getAll(Map<String, String> params);
}
