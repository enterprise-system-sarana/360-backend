package com.saranaresturantsystem.common;

import com.saranaresturantsystem.repository.common.InvoiceSequenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceNumberServiceTest {

    @Mock
    private InvoiceSequenceRepository sequenceRepository;

    @InjectMocks
    private InvoiceNumberService invoiceNumberService;

    @Test
    void testGenerate_IncrementsSequentially() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String expectedKey = "POS-" + today;

        when(sequenceRepository.getNextValue(eq(expectedKey)))
                .thenReturn(1L)
                .thenReturn(2L)
                .thenReturn(10L);

        String first = invoiceNumberService.generate("POS");
        String second = invoiceNumberService.generate("SALE"); // Should also normalize SALE -> POS
        String tenth = invoiceNumberService.generate("POS");

        assertEquals("POS-" + today + "-0001", first);
        assertEquals("POS-" + today + "-0002", second);
        assertEquals("POS-" + today + "-0010", tenth);
    }

    @Test
    void testGenerate_HandlesNullOrEmptyPrefix() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String defaultKey = "INV-" + today;

        when(sequenceRepository.getNextValue(eq(defaultKey))).thenReturn(1L);

        String resultWithNull = invoiceNumberService.generate(null);
        assertEquals("INV-" + today + "-0001", resultWithNull);

        String resultWithEmpty = invoiceNumberService.generate("   ");
        assertEquals("INV-" + today + "-0001", resultWithEmpty);
    }
}
