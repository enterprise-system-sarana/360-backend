package com.saranaresturantsystem.services.impl.finances;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.common.UniqueChecker;
import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.dto.request.finances.BankRequest;
import com.saranaresturantsystem.dto.response.finances.BankResponse;
import com.saranaresturantsystem.entities.finances.Banks;
import com.saranaresturantsystem.execption.DuplicateResourceException;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.finances.BankMapper;
import com.saranaresturantsystem.repository.finances.BackRepository;
import com.saranaresturantsystem.services.interfaces.finances.BankService;
import com.saranaresturantsystem.specification.finances.BankFilter;
import com.saranaresturantsystem.specification.finances.BankSpec;
import com.saranaresturantsystem.utils.PageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.saranaresturantsystem.constants.Constants.STATUS_DELETE;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankServiceImpl implements BankService {

    private final BackRepository bankRepository;
    private final ObjectMapper objectMapper;
    private final BankMapper bankMapper;
    private final UniqueChecker uniqueChecker;

    @Override
    @Transactional(readOnly = true)
    public Page<BankResponse> getListBank(Map<String, String> params) {
        BankFilter bankFilter = objectMapper.convertValue(params, BankFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Banks> spec = BankSpec.filterBy(bankFilter);
        return bankRepository.findAll(spec, pageable).map(bankMapper::toBankResponse);
    }

    @Override
    @Transactional
    public Banks getBankById(long id) {
        Banks existingBank = bankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank", id));

        if (Constants.STATUS_INIT.equals(existingBank.getStatus()) || STATUS_DELETE.equals(existingBank.getStatus())) {
            throw new ResourceNotFoundException("Bank", id);
        }
        return existingBank;
    }

    @Override
    @Transactional
    public BankResponse createBank(@Valid BankRequest bankRequest) {
        Banks bank = bankMapper.toEntity(bankRequest);

        uniqueChecker.verify(bankRepository, bank, "accountName", bank.getAccountName());
        uniqueChecker.verify(bankRepository, bank, "accountNumber", bank.getAccountNumber());

        bank.setStatus(Constants.STATUS_ACTIVE);
        Banks savedBank = bankRepository.save(bank);
        return bankMapper.toBankResponse(savedBank);
    }

    @Override
    @Transactional
    public BankResponse updateBank(Long id, @Valid BankRequest bankRequest) {
        Banks bank = getBankById(id);

        uniqueChecker.verify(bankRepository, bank, "accountName", bankRequest.accountName());
        uniqueChecker.verify(bankRepository, bank, "accountNumber", bankRequest.accountNumber());

        bankMapper.updateEntityFromRequest(bankRequest, bank);


        Banks updatedBank = bankRepository.save(bank);
        return bankMapper.toBankResponse(updatedBank);
    }

    @Override
    @Transactional(readOnly = true)
    public BankResponse getBankResponseById(Long id) {
        return bankMapper.toBankResponse(getBankById(id));
    }

    @Override
    @Transactional
    public void deleteBank(Long id) {
        Banks bank = getBankById(id);
        bank.setStatus(STATUS_DELETE);
        bankRepository.save(bank);
    }
}