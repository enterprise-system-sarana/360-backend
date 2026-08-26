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

import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class CurrencyServiceImpl implements CurrencyService {
    private  final CurrencyRepository currencyRepository ;
    private  final UniqueChecker uniqueChecker ;
    private  final ObjectMapper objectMapper ;
    private  final CurrencyMapper currencyMapper ;

    @Override
    public Page<CurrencyResponse> findAll(Map<String, String> params) {
        CurrencyFilter filter =objectMapper.convertValue(params , CurrencyFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Currency> spec = CurrencySpec.filterBy(filter);
        return  currencyRepository.findAll(spec , pageable).map(currencyMapper::toResponse);
    }

    @Override
    public CurrencyResponse save(CurrencyRequest request) {
        Currency currency = currencyMapper.toEntity(request);
        uniqueChecker.verify(currencyRepository , currency , "name" , currency.getName());
        uniqueChecker.verify(currencyRepository , currency , "code" , currency.getCode());
        currency.setStatus(Constants.STATUS_ACTIVE);
        Currency saveCurrency = currencyRepository.save(currency);
        return currencyMapper.toResponse(saveCurrency);
    }

    @Override
    public CurrencyResponse update(CurrencyRequest request, Long id) {
        Currency currency = findById(id);
        uniqueChecker.verify(currencyRepository , currency , "name", request.name());
        uniqueChecker.verify(currencyRepository , currency , "code", request.code());
        currencyMapper.updateEntityFromRequest( request , currency);
        return currencyMapper.toResponse(currency);
    }

    @Override
    public CurrencyResponse getById(Long id) {
        Currency findId = findById(id);
        return  currencyMapper.toResponse(findId);
    }

    @Override
    public Currency findById(Long id) {
        Currency findCurrency = currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency : " + id));
        if (findCurrency.getStatus().equals(Constants.STATUS_INIT) || findCurrency.getStatus().equals(Constants.STATUS_DELETE)) {
            throw new ResourceNotFoundException("Currency : " + id);
        }
        return  findCurrency;
    }

    @Override
    public void delete(Long id) {
        Currency currency = findById(id);
        currency.setStatus(Constants.STATUS_DELETE);
        currencyRepository.save(currency);
    }
}
