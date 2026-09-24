package com.saranaresturantsystem.services.impl.reports;

import com.saranaresturantsystem.dto.response.finance.ProfitLossReportResponse;
import com.saranaresturantsystem.repository.purchases.ExpenseRepository;
import com.saranaresturantsystem.repository.purchases.PurchasesRepository;
import com.saranaresturantsystem.repository.sales.SaleRepository;
import com.saranaresturantsystem.services.interfaces.reports.ProfitLossReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfitLossReportServiceImpl implements ProfitLossReportService {

    private final SaleRepository saleRepository;
    private final PurchasesRepository purchasesRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    public ProfitLossReportResponse generateProfitLossReport(LocalDate startDate, LocalDate endDate, Integer months) {
        if (startDate == null) {
            int pastMonths = (months != null && months > 0) ? months - 1 : 5;
            startDate = LocalDate.now().minusMonths(pastMonths).withDayOfMonth(1);
        }
        if (endDate == null) endDate = LocalDate.now();

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<Object[]> salesIncomeRaw = saleRepository.getSalesIncomeByMonth(start, end);
        List<Object[]> salesReturnRaw = saleRepository.getSalesReturnByMonth(start, end);
        List<Object[]> purchaseExpenseRaw = purchasesRepository.getPurchaseExpenseByMonth(startDate, endDate);
        List<Object[]> salesExpenseRaw = expenseRepository.getSalesExpenseByMonth(start, end);

        List<String> monthLabels = new ArrayList<>();
        List<BigDecimal> salesList = new ArrayList<>();
        List<BigDecimal> serviceList = new ArrayList<>();
        List<BigDecimal> purchaseReturnList = new ArrayList<>();
        List<BigDecimal> grossProfitList = new ArrayList<>();
        
        List<BigDecimal> salesExpenseList = new ArrayList<>();
        List<BigDecimal> purchaseList = new ArrayList<>();
        List<BigDecimal> salesReturnList = new ArrayList<>();
        List<BigDecimal> totalExpenseList = new ArrayList<>();
        
        List<BigDecimal> netProfitList = new ArrayList<>();

        YearMonth currentMonth = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);

        while (!currentMonth.isAfter(endMonth)) {
            monthLabels.add(currentMonth.format(DateTimeFormatter.ofPattern("MMM yyyy")));

            BigDecimal monthSales = getAmountForMonth(salesIncomeRaw, currentMonth);
            BigDecimal monthService = BigDecimal.ZERO; 
            BigDecimal monthPurchaseReturn = BigDecimal.ZERO;
            BigDecimal monthGrossProfit = monthSales.add(monthService).add(monthPurchaseReturn);

            BigDecimal monthSalesExpense = getAmountForMonth(salesExpenseRaw, currentMonth);
            BigDecimal monthPurchase = getAmountForMonth(purchaseExpenseRaw, currentMonth);
            BigDecimal monthSalesReturn = getAmountForMonth(salesReturnRaw, currentMonth);
            BigDecimal monthTotalExpense = monthSalesExpense.add(monthPurchase).add(monthSalesReturn);

            BigDecimal monthNetProfit = monthGrossProfit.subtract(monthTotalExpense);

            salesList.add(monthSales);
            serviceList.add(monthService);
            purchaseReturnList.add(monthPurchaseReturn);
            grossProfitList.add(monthGrossProfit);

            salesExpenseList.add(monthSalesExpense);
            purchaseList.add(monthPurchase);
            salesReturnList.add(monthSalesReturn);
            totalExpenseList.add(monthTotalExpense);

            netProfitList.add(monthNetProfit);

            currentMonth = currentMonth.plusMonths(1);
        }

        return ProfitLossReportResponse.builder()
                .months(monthLabels)
                .income(ProfitLossReportResponse.IncomeData.builder()
                        .sales(salesList)
                        .service(serviceList)
                        .purchaseReturn(purchaseReturnList)
                        .grossProfit(grossProfitList)
                        .build())
                .expenses(ProfitLossReportResponse.ExpenseData.builder()
                        .sales(salesExpenseList)
                        .purchase(purchaseList)
                        .salesReturn(salesReturnList)
                        .totalExpense(totalExpenseList)
                        .build())
                .netProfit(netProfitList)
                .build();
    }

    private BigDecimal getAmountForMonth(List<Object[]> rawData, YearMonth targetMonth) {
        if (rawData == null) return BigDecimal.ZERO;
        for (Object[] record : rawData) {
            int recordYear = ((Number) record[0]).intValue();
            int recordMonth = ((Number) record[1]).intValue();
            if (recordYear == targetMonth.getYear() && recordMonth == targetMonth.getMonthValue()) {
                if (record[2] != null) {
                    return BigDecimal.valueOf(((Number) record[2]).doubleValue());
                }
            }
        }
        return BigDecimal.ZERO;
    }
}
