package com.saranaresturantsystem.common;

import org.springframework.stereotype.Service;


@Service
public  class InvoiceService {
    public String generate(String prefix  ) {
        return prefix + "-" +  System.currentTimeMillis();
    }
}
