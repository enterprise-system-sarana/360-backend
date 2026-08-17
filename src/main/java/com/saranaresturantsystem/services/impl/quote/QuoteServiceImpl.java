package com.saranaresturantsystem.services.impl.quote;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.dto.request.quote.QuoteItemRequest;
import com.saranaresturantsystem.dto.request.quote.QuoteRequest;
import com.saranaresturantsystem.dto.response.quote.QuoteResponse;

import com.saranaresturantsystem.entities.catalog.Product;
import com.saranaresturantsystem.entities.catalog.ProductSerials;
import com.saranaresturantsystem.entities.customer.Customer;
import com.saranaresturantsystem.entities.quote.Quote;
import com.saranaresturantsystem.entities.quote.QuoteItem;
import com.saranaresturantsystem.enums.StatusType;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.quote.QuoteMapper;
import com.saranaresturantsystem.repository.catalog.ProductRepository;
import com.saranaresturantsystem.repository.catalog.ProductSerialsRepository;
import com.saranaresturantsystem.repository.customer.CustomerRepository;
import com.saranaresturantsystem.repository.quote.QuoteRepository;
import com.saranaresturantsystem.services.interfaces.quote.QuoteService;
import com.saranaresturantsystem.specification.quote.QuoteFilter;
import com.saranaresturantsystem.specification.quote.QuoteSpec;
import com.saranaresturantsystem.utils.PageUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteService {
    private final QuoteRepository quoteRepository;
    private final CustomerRepository customerRepository;
    private final ObjectMapper objectMapper;
    private final QuoteMapper  quoteMapper;
    private final ProductRepository productRepository;
    private final ProductSerialsRepository productSerialsRepository;


    @Override
    public Page<QuoteResponse> getList(Map<String, String> params) {
        QuoteFilter filter = objectMapper.convertValue(params, QuoteFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Quote> spec = QuoteSpec.filterBy(filter);
        return quoteRepository.findAll(spec, pageable)
                .map(quoteMapper::toResponse);
    }

    @Override
    public QuoteResponse findById(@Positive Long id) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quote", id));
        return quoteMapper.toResponse(quote);
    }

    @Override
    @Transactional
    public QuoteResponse createQuote(QuoteRequest request) {
        Customer customer=customerRepository.findById(request.customerId())
                .orElseThrow(()->new ResourceNotFoundException("Customer",request.customerId()));
        Quote quote=new Quote();
        quote.setCustomer(customer);
        quote.setDate(request.date() != null ? request.date() : LocalDateTime.now());
        quote.setReference(request.reference());
        quote.setNo(request.no());
        quote.setNoted(request.noted());
        quote.setDiscount(request.discount() !=null ? request.discount(): BigDecimal.ZERO);
        quote.setPaid_amount(request.paid_amount() !=null ? request.paid_amount(): BigDecimal.ZERO);
        quote.setReturn_amount(request.return_amount() !=null ? request.return_amount(): BigDecimal.ZERO);
        quote.setStatus(request.status() != null ? request.status() : StatusType.ACTIVE);
        quote.setStatus_payment(request.status_payment());
        processQuoteItems(quote,request.items());
        Quote savedQuote=quoteRepository.save(quote);
        return quoteMapper.toResponse(savedQuote);
    }

    private void processQuoteItems(Quote quote, List<QuoteItemRequest> itemRequest) {
        if (itemRequest == null || itemRequest.isEmpty()) {
            quote.setGrandTotal(BigDecimal.ZERO);
            return;
        }
        List<QuoteItem> items = quote.getQuoteItems() != null ? quote.getQuoteItems() : new ArrayList<>();
        BigDecimal itemsTotalSum = BigDecimal.ZERO;
        for (QuoteItemRequest itemReq : itemRequest) {
            Product product = productRepository.findById(itemReq.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", itemReq.productId()));

            ProductSerials productSerial = null;
            if (itemReq.product_serial() != null) {
                productSerial = productSerialsRepository.findById(itemReq.product_serial())
                        .orElseThrow(() -> new ResourceNotFoundException("ProductSerial", itemReq.product_serial()));
            }
            QuoteItem item = new QuoteItem();
            item.setQuote(quote);
            item.setProduct(product);
            item.setProduct_serials(productSerial);

            BigDecimal qty = itemReq.qty() != null ? itemReq.qty() : BigDecimal.ZERO;

            BigDecimal price = itemReq.price() != null ? itemReq.price() : BigDecimal.ZERO;
            BigDecimal discount = itemReq.discount_item() != null ? itemReq.discount_item() : BigDecimal.ZERO;

            BigDecimal itemSubtotal = price.multiply(qty).subtract(discount);

            item.setQty(qty);
            item.setPrice(price);
            item.setDiscount_item(discount);
            item.setSubtotal(itemSubtotal);

            items.add(item);
            itemsTotalSum = itemsTotalSum.add(itemSubtotal);
        }

        quote.setQuoteItems(items);

        BigDecimal globalDiscount = quote.getDiscount() != null ? quote.getDiscount() : BigDecimal.ZERO;
        BigDecimal grandTotal = itemsTotalSum.subtract(globalDiscount);

        quote.setGrandTotal(grandTotal.max(BigDecimal.ZERO));
    }
    @Override
    public QuoteResponse update(Long id, QuoteRequest request) {
        return null;
    }

    @Override
    @Transactional
    public void deleteQuote(Long id) {

        Quote existing = quoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quote", id));
        existing.setStatus(StatusType.INACTIVE);
        quoteRepository.save(existing);
    }
}
