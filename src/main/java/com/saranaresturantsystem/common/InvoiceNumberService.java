package com.saranaresturantsystem.common;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class InvoiceNumberService {

    private final AtomicInteger sequence = new AtomicInteger(1);

    public String generate(String prefix) {

        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddss"));

        int number = sequence.getAndIncrement();

        return String.format(
                "%s-%s-%04d",
                prefix,
                date,
                number + 1
        );
    }
}