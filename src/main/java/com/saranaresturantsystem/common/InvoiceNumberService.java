package com.saranaresturantsystem.common;

import com.saranaresturantsystem.repository.common.InvoiceSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class InvoiceNumberService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final InvoiceSequenceRepository sequenceRepository;

    /**
     * Generates a unique, sequentially increasing invoice number: {PREFIX}-{yyyyMMdd}-{0001}
     * Persisted in PostgreSQL to ensure no duplicates across restarts and concurrent requests.
     *
     * @param prefix Prefix code (e.g. "SALE", "QUOTE", "EXPENSE")
     * @return Formatted invoice number string
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generate(String prefix) {
        String cleanPrefix = normalizePrefix(prefix);
        String date = LocalDate.now().format(DATE_FORMATTER);
        String sequenceKey = cleanPrefix + "-" + date;

        Long sequenceNumber = sequenceRepository.getNextValue(sequenceKey);

        return String.format("%s-%s-%04d", cleanPrefix, date, sequenceNumber);
    }

    private String normalizePrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return "INV";
        }
        String upper = prefix.trim().toUpperCase();
        return switch (upper) {
            case "SALE", "SALES", "POS" -> "POS";
            case "PURCHASE", "PURCHASES", "PUR" -> "PUR";
            case "EXPENSE", "EXPENSES", "EXP" -> "EXP";
            case "QUOTATION", "QUOTE", "QUOTES", "QT" -> "QT";
            default -> upper;
        };
    }
}