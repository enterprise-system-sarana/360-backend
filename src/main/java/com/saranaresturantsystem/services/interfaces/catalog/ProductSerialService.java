package com.saranaresturantsystem.services.interfaces.catalog;

import com.saranaresturantsystem.dto.response.catalog.ProductSerialResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ProductSerialService {
   Page<ProductSerialResponse> findAll(Map<String , String> params);

//    Map<String , String> findAll(Map<String , String> params);

}
