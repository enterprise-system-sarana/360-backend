package com.saranaresturantsystem.services.impl.catalog;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.dto.response.catalog.ProductSerialResponse;
import com.saranaresturantsystem.entities.catalog.ProductSerials;
import com.saranaresturantsystem.mappers.catalog.ProductSerialMapper;
import com.saranaresturantsystem.repository.catalog.ProductSerialRepository;
import com.saranaresturantsystem.services.interfaces.catalog.ProductSerialService;
import com.saranaresturantsystem.specification.catalog.productSerial.ProductSerialFilter;
import com.saranaresturantsystem.specification.catalog.productSerial.ProductSerialSpec;
import com.saranaresturantsystem.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ProductSerialServiceImpl implements ProductSerialService {
    private  final ProductSerialRepository serialRepository ;
    private  final ObjectMapper objectMapper  ;
    private  final ProductSerialMapper serialMapper;
    @Override
    public Page<ProductSerialResponse> findAll(Map<String, String> params) {
        ProductSerialFilter filter = objectMapper.convertValue(params, ProductSerialFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<ProductSerials> spec = ProductSerialSpec.filterBy(filter);
        return  serialRepository.findAll(spec , pageable).map(serialMapper::toResponse);
    }
}
