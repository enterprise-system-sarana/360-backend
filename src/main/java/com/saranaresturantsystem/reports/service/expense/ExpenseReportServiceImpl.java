package com.saranaresturantsystem.reports.service.expense;


import com.saranaresturantsystem.entities.purchase.Expenses;
import com.saranaresturantsystem.reports.specification.expenses.ExpenseReportFilter;
import com.saranaresturantsystem.reports.specification.expenses.ExpenseReportSpec;
import com.saranaresturantsystem.repository.purchases.ExpenseRepository;
import com.saranaresturantsystem.reports.dto.expenses.ExpenseReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExpenseReportServiceImpl implements ExpenseReportService {

    private final ExpenseRepository expenseRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseReportResponse> getExpensesReport(ExpenseReportFilter filter, Pageable pageable) {
        Specification<Expenses> spec = ExpenseReportSpec.filter(filter);

        return expenseRepository.findAll(spec, pageable).map(expense ->
                ExpenseReportResponse.builder()
                        .id(expense.getId())
                        .reference(expense.getReference())
                        .amount(expense.getAmount())
                        .note(expense.getNote())
                        .storeId(expense.getStores().getId())
                        .storeName(expense.getStores().getName())
                        .status(expense.getStatus())
                        .bankId(expense.getBank() != null ? expense.getBank().getId() : null)
                        .bankName(expense.getBank() != null ? expense.getBank().getName() : null)
                        .expenseTypeId(expense.getExpenseType() != null ? expense.getExpenseType().getId() : null)
                        .expenseTypeName(expense.getExpenseType() != null ? expense.getExpenseType().getName() : null)
                        .createdAt(expense.getCreatedAt())
                        .build()
        );
    }
}