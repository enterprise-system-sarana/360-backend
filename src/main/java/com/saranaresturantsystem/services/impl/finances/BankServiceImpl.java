package com.saranaresturantsystem.services.impl.finances;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.common.UniqueChecker;
import com.saranaresturantsystem.dto.request.finances.BankRequest;
import com.saranaresturantsystem.dto.response.finances.BankResponse;
import com.saranaresturantsystem.entities.finances.Banks;
import com.saranaresturantsystem.enums.StatusType;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.finances.BankMapper;
import com.saranaresturantsystem.repository.finances.BackRepository;
import com.saranaresturantsystem.services.interfaces.finances.BankService;
import com.saranaresturantsystem.specification.finances.BankFilter;
import com.saranaresturantsystem.specification.finances.BankSpec;
import com.saranaresturantsystem.utils.PageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import java.util.Map;

@RequiredArgsConstructor
@Service

public class BankServiceImpl implements BankService {
    private final BackRepository bankRepository;
    private final ObjectMapper objectMapper;
    private final BankMapper bankMapper;
    private final UniqueChecker uniqueChecker;

    @Override
    public Page<BankResponse> getListBank(Map<String, String> params) {
        BankFilter bankFilter = objectMapper.convertValue(params, BankFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Banks> spec = BankSpec.filterBy(bankFilter);
        return bankRepository.findAll(spec, pageable).map(bankMapper::toBankResponse);
    }

    @Cacheable(value = "banks", key = "#id")
    @Override
    public Banks getBankById(long id) {
        Banks exitId = bankRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bank", id));
        if (exitId.getStatus() == StatusType.INACTIVE || exitId.getStatus() == null) {
            throw new ResourceNotFoundException("Bank", id);
        }
        return exitId;
    }

    @Override
    public BankResponse createBank(@Valid BankRequest bankRequest) {
        Banks bank = bankMapper.toEntity(bankRequest);
        uniqueChecker.verify(bankRepository, bank, "name", bank.getName());
        uniqueChecker.verify(bankRepository, bank, "accountNumber", bank.getAccountNumber());
        Banks savedBank = bankRepository.save(bank);
        return bankMapper.toBankResponse(savedBank);
    }

    @CacheEvict(value = "banks", key = "#id")
    @Override
    public BankResponse updateBank(Long id, BankRequest bankRequest) {
        Banks bank = getBankById(id);
        bankMapper.updateEntityFromRequest(bankRequest, bank);
        Banks updateBank = bankRepository.save(bank);
        return bankMapper.toBankResponse(updateBank);
    }

    @Cacheable(value = "banks", key = "#id")
    @Override
    public BankResponse getBankResponseById(Long id) {
        return bankMapper.toBankResponse(getBankById(id));
    }

    @CacheEvict(value = "banks", key = "#id")
    @Override
    public void deleteBank(Long id) {
        Banks bank = getBankById(id);
        bank.setStatus(StatusType.INACTIVE);
        bankRepository.save(bank);
    }
}
