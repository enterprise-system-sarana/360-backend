package com.saranaresturantsystem.services.interfaces.finances;

import com.saranaresturantsystem.dto.request.finances.BankRequest;
import com.saranaresturantsystem.dto.response.finances.BankResponse;
import com.saranaresturantsystem.entities.finances.Banks;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface BankService {

    Page<BankResponse> getListBank(Map<String, String> params);

    Banks getBankById(long id);

    BankResponse createBank(BankRequest bankRequest);

    BankResponse updateBank(Long id, BankRequest bankRequest);

    BankResponse getBankResponseById(Long id);

    void deleteBank(Long id);
}
