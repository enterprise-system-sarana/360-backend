package com.saranaresturantsystem.services.interfaces.finances;

import com.saranaresturantsystem.dto.request.finances.CurrencyRequest;
import com.saranaresturantsystem.dto.response.finances.CurrencyResponse;
import com.saranaresturantsystem.entities.finances.Currency;
import org.springframework.data.domain.Page;


import java.util.Map;

public interface CurrencyService {
    Page<CurrencyResponse>findAll(Map<String , String> params);
    CurrencyResponse save (CurrencyRequest request);
    CurrencyResponse update(CurrencyRequest request , Long id );
    CurrencyResponse getById(Long id);
    Currency findById(Long id);
    void delete (Long id );
}
