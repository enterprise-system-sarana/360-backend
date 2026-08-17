package com.saranaresturantsystem.services.interfaces.quote;

import com.saranaresturantsystem.dto.request.quote.QuoteRequest;
import com.saranaresturantsystem.dto.response.quote.QuoteResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface QuoteService {
    Page<QuoteResponse> getList(Map<String, String> params);

    QuoteResponse findById(@Positive Long id);

    QuoteResponse createQuote(@Valid QuoteRequest request);
    QuoteResponse update(Long id, QuoteRequest request);
    void deleteQuote(Long id);
}
