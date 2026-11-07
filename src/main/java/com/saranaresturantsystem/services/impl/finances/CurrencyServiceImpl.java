package com.saranaresturantsystem.services.impl.finances;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.common.UniqueChecker;
import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.dto.request.finances.CurrencyRequest;
import com.saranaresturantsystem.dto.response.finances.CurrencyResponse;
import com.saranaresturantsystem.entities.finances.Currency;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.finances.CurrencyMapper;
import com.saranaresturantsystem.repository.finances.CurrencyRepository;
import com.saranaresturantsystem.services.interfaces.finances.CurrencyService;
import com.saranaresturantsystem.specification.finances.currency.CurrencyFilter;
import com.saranaresturantsystem.specification.finances.currency.CurrencySpec;
import com.saranaresturantsystem.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final UniqueChecker uniqueChecker;
    private final ObjectMapper objectMapper;
    private final CurrencyMapper currencyMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<CurrencyResponse> findAll(Map<String, String> params) {
        CurrencyFilter filter = objectMapper.convertValue(params, CurrencyFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Currency> spec = CurrencySpec.filterBy(filter);
        return currencyRepository.findAll(spec, pageable).map(currencyMapper::toResponse);
    }

    @Override
    @Transactional
    public CurrencyResponse save(CurrencyRequest request) {
        log.info("Creating new Currency with code: {}", request.code());
        Currency currency = currencyMapper.toEntity(request);
        uniqueChecker.verify(currencyRepository, currency, "name", currency.getName());
        uniqueChecker.verify(currencyRepository, currency, "code", currency.getCode());
        if (currency.getStatus() == null || currency.getStatus().isBlank()) {
            currency.setStatus(Constants.STATUS_ACTIVE);
        }
        Currency savedCurrency = currencyRepository.save(currency);
        log.info("Currency created successfully with ID: {}", savedCurrency.getId());
        return currencyMapper.toResponse(savedCurrency);
    }

    @Override
    @Transactional
    public CurrencyResponse update(CurrencyRequest request, Long id) {
        log.info("Updating Currency with ID: {}", id);
        Currency currency = findById(id);

        uniqueChecker.verify(currencyRepository, currency, "name", request.name());
        uniqueChecker.verify(currencyRepository, currency, "code", request.code());

        currencyMapper.updateEntityFromRequest(request, currency);

        if (request.status() != null && !request.status().isBlank()) {
            currency.setStatus(request.status());
        }

        Currency savedCurrency = currencyRepository.save(currency);
        log.info("Currency updated successfully with ID: {}", savedCurrency.getId());
        return currencyMapper.toResponse(savedCurrency);
    }

    @Override
    @Transactional(readOnly = true)
    public CurrencyResponse getById(Long id) {
        Currency findId = findById(id);
        return currencyMapper.toResponse(findId);
    }

    @Override
    @Transactional(readOnly = true)
    public Currency findById(Long id) {
        Currency findCurrency = currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency : " + id));
        if (Constants.STATUS_INIT.equals(findCurrency.getStatus())) {
            throw new ResourceNotFoundException("Currency : " + id);
        }
        return findCurrency;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting Currency with ID: {}", id);
        Currency currency = findById(id);
        currency.setStatus(Constants.STATUS_INACTIVE);
        currencyRepository.save(currency);
        log.info("Currency marked inactive successfully with ID: {}", id);
    }
}
