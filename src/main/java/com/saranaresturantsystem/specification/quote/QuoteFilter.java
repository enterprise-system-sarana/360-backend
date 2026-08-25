package com.saranaresturantsystem.specification.quote;

import com.saranaresturantsystem.specification.common.StatusFilter;
import java.time.LocalDate;

public record QuoteFilter(
        Long id,
        LocalDate date,
        String reference,
        String no,
        Long customerId,
        String status,
        String paymentStatus
) implements StatusFilter {
}
