package com.saranaresturantsystem.reports.service.finance;

import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.entities.finances.Banks;
import com.saranaresturantsystem.repository.finances.BankRepository;
import com.saranaresturantsystem.reports.dto.bank.BankReportFilter;
import com.saranaresturantsystem.reports.dto.bank.BankReportResponse;
import com.saranaresturantsystem.reports.dto.bank.BankReportSummaryProjection;
import com.saranaresturantsystem.reports.dto.bank.BankReportSummaryResponse;
import com.saranaresturantsystem.reports.entities.ViewBankReport;
import com.saranaresturantsystem.reports.repository.ViewBankReportRepository;
import com.saranaresturantsystem.reports.specification.finance.BankReportSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankReportServiceImpl implements BankReportService {

    private final ViewBankReportRepository viewBankReportRepository;
    private final BankRepository bankRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "report:banks",
            key = "'summary:' + (#filter != null ? #filter.hashCode() : 'all')",
            unless = "#result == null"
    )
    public BankReportSummaryResponse getBankReportSummary(BankReportFilter filter) {
        log.info("Generating bank report summary for filter: {}", filter);

        LocalDateTime startDateTime = filter != null && filter.startDate() != null
                ? filter.startDate().atStartOfDay()
                : null;
        LocalDateTime endDateTime = filter != null && filter.endDate() != null
                ? filter.endDate().atTime(LocalTime.MAX)
                : null;

        Long bankId = filter != null ? filter.bankId() : null;
        String transactionType = filter != null ? filter.transactionType() : null;
        String flowType = filter != null ? filter.flowType() : null;

        BankReportSummaryProjection summary = viewBankReportRepository.getSummary(
                bankId,
                transactionType,
                flowType,
                startDateTime,
                endDateTime
        );

        BigDecimal totalInflow = summary != null && summary.getTotalInflow() != null
                ? summary.getTotalInflow()
                : BigDecimal.ZERO;
        BigDecimal totalOutflow = summary != null && summary.getTotalOutflow() != null
                ? summary.getTotalOutflow()
                : BigDecimal.ZERO;
        Long totalTransactions = summary != null && summary.getTotalTransactions() != null
                ? summary.getTotalTransactions()
                : 0L;

        BigDecimal netFlow = totalInflow.subtract(totalOutflow);

        // Compute current total balance
        BigDecimal currentTotalBalance = BigDecimal.ZERO;
        if (bankId != null) {
            currentTotalBalance = bankRepository.findById(bankId)
                    .map(b -> b.getCurrentBalance() != null ? b.getCurrentBalance() : (b.getOpeningBalance() != null ? b.getOpeningBalance() : BigDecimal.ZERO))
                    .orElse(BigDecimal.ZERO);
        } else {
            List<Banks> allBanks = bankRepository.findAll();
            for (Banks b : allBanks) {
                if (!Constants.STATUS_DELETE.equals(b.getStatus())) {
                    BigDecimal bal = b.getCurrentBalance() != null
                            ? b.getCurrentBalance()
                            : (b.getOpeningBalance() != null ? b.getOpeningBalance() : BigDecimal.ZERO);
                    currentTotalBalance = currentTotalBalance.add(bal);
                }
            }
        }

        Specification<ViewBankReport> spec = BankReportSpec.filter(filter);
        List<BankReportResponse> transactions = viewBankReportRepository.findAll(spec).stream()
                .map(this::mapToResponse)
                .toList();

        return BankReportSummaryResponse.builder()
                .totalInflow(totalInflow)
                .totalOutflow(totalOutflow)
                .netFlow(netFlow)
                .totalTransactions(totalTransactions)
                .currentTotalBalance(currentTotalBalance)
                .transactions(transactions)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "report:banks",
            key = "'paged:' + (#filter != null ? #filter.hashCode() : 'all') + ':' + #pageable.pageNumber + ':' + #pageable.pageSize",
            unless = "#result == null"
    )
    public Page<BankReportResponse> getBankReportPaged(BankReportFilter filter, Pageable pageable) {
        Specification<ViewBankReport> spec = BankReportSpec.filter(filter);
        return viewBankReportRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    private BankReportResponse mapToResponse(ViewBankReport v) {
        return BankReportResponse.builder()
                .transactionId(v.getTransactionId())
                .bankId(v.getBankId())
                .bankName(v.getBankName())
                .accountName(v.getAccountName())
                .accountNumber(v.getAccountNumber())
                .bankCurrentBalance(v.getBankCurrentBalance())
                .amount(v.getAmount())
                .transactionType(v.getTransactionType())
                .transactionReference(v.getTransactionReference())
                .transactionStatus(v.getTransactionStatus())
                .transactionDate(v.getTransactionDate())
                .description(v.getDescription())
                .saleId(v.getSaleId())
                .saleNo(v.getSaleNo())
                .saleReference(v.getSaleReference())
                .purchaseId(v.getPurchaseId())
                .purchaseReferenceNo(v.getPurchaseReferenceNo())
                .expenseId(v.getExpenseId())
                .expenseReference(v.getExpenseReference())
                .flowType(v.getFlowType())
                .createdAt(v.getCreatedAt())
                .build();
    }
}

