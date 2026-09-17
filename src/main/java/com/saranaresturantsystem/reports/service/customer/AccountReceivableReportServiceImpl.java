package com.saranaresturantsystem.reports.service.customer;

import com.saranaresturantsystem.entities.sales.Sales;
import com.saranaresturantsystem.reports.dto.customers.AccountReceivableReportResponse;
import com.saranaresturantsystem.reports.specification.customers.AccountReceivableReportFilter;
import com.saranaresturantsystem.reports.specification.customers.AccountReceivableReportSpec;
import com.saranaresturantsystem.repository.sales.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountReceivableReportServiceImpl implements AccountReceivableReportService {

    private final SaleRepository saleRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<AccountReceivableReportResponse> getAccountReceivableReport(AccountReceivableReportFilter filter, Pageable pageable) {
        Specification<Sales> spec = AccountReceivableReportSpec.filter(filter);

        return saleRepository.findAll(spec, pageable).map(sale ->
                AccountReceivableReportResponse.builder()
                        .id(sale.getId())
                        .date(sale.getCreatedAt() != null ? sale.getCreatedAt().toLocalDate() : null)
                        .customerName(sale.getCustomer() != null ? sale.getCustomer().getName() : "Walk-in")
                        .phone(sale.getCustomer() != null ? sale.getCustomer().getPhone() : null)
                        .addressOrNote(sale.getNoted())
                        // Convert Double to BigDecimal if sale.getPaidAmount() returns Double,
                        // or just leave as sale.getPaidAmount() if it's already BigDecimal:
                        .amount(sale.getPaidAmount() != null ? BigDecimal.valueOf(sale.getPaidAmount()) : BigDecimal.ZERO)
                        .storeId(sale.getStore() != null ? sale.getStore().getId() : null)
                        .storeName(sale.getStore() != null ? sale.getStore().getName() : null)
                        .build()
        );
    }
}