package com.saranaresturantsystem.reports.service.sales;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.dto.response.sales.SaleResponse;
import com.saranaresturantsystem.entities.sales.Sales;
import com.saranaresturantsystem.mappers.sale.SaleMapper;
import com.saranaresturantsystem.repository.sales.SaleRepository;
import com.saranaresturantsystem.reports.dto.sales.SaleReportFilter;
import com.saranaresturantsystem.reports.dto.sales.SaleReportResponse;
import com.saranaresturantsystem.reports.specification.sales.SaleReportSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final SaleRepository saleRepository;
    private final SaleMapper saleMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public SaleReportResponse getSaleReport(SaleReportFilter filter) {
        Specification<Sales> spec = SaleReportSpec.filter(filter);

        List<Sales> salesList = saleRepository.findAll(spec);

        double totalSalesAmount = salesList.stream()
                .mapToDouble(s -> s.getGrandTotal() != null ? s.getGrandTotal() : 0.0)
                .sum();

        double totalDiscount = salesList.stream()
                .mapToDouble(s -> s.getDiscount() != null ? s.getDiscount() : 0.0)
                .sum();

        double totalPaidAmount = salesList.stream()
                .mapToDouble(s -> s.getPaidAmount() != null ? s.getPaidAmount() : 0.0)
                .sum();

        List<SaleResponse> saleResponses = salesList.stream()
                .map(saleMapper::toResponse)
                .toList();

        return SaleReportResponse.builder()
                .totalSalesAmount(totalSalesAmount)
                .totalDiscount(totalDiscount)
                .totalPaidAmount(totalPaidAmount)
                .totalTransactions((long) salesList.size())
                .storeId(filter.storeId())
                .customerId(filter.customerId())
                .startDate(filter.startDate() != null ? filter.startDate().atStartOfDay() : null)
                .endDate(filter.endDate() != null ? filter.endDate().atTime(23, 59, 59) : null)
                .sales(saleResponses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SaleReportResponse> getSalesItemsReport(SaleReportFilter filter, Pageable pageable) {
        Specification<Sales> spec = SaleReportSpec.filter(filter);

        return saleRepository.findAll(spec, pageable).map(sale -> {
            SaleResponse saleResponse = saleMapper.toResponse(sale);
            return SaleReportResponse.builder()
                    .totalSalesAmount(sale.getGrandTotal() != null ? sale.getGrandTotal() : 0.0)
                    .totalDiscount(sale.getDiscount() != null ? sale.getDiscount() : 0.0)
                    .totalPaidAmount(sale.getPaidAmount() != null ? sale.getPaidAmount() : 0.0)
                    .totalTransactions(1L)
                    .sales(List.of(saleResponse))
                    .build();
        });
    }
}