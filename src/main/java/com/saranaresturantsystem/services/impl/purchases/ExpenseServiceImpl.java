package com.saranaresturantsystem.services.impl.purchases;

import com.saranaresturantsystem.dto.request.purchases.ExpenseRequest;
import com.saranaresturantsystem.dto.response.purchases.ExpenseResponse;
import com.saranaresturantsystem.entities.finances.Bank_Transactions;
import com.saranaresturantsystem.entities.purchase.Expenses;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.purchase.ExpenseMapper;
import com.saranaresturantsystem.repository.finances.BackTransactionRepository;
import com.saranaresturantsystem.repository.purchases.ExpenseRepository;
import com.saranaresturantsystem.services.interfaces.purchases.ExpenseService;
import com.saranaresturantsystem.specification.purchases.Expenses.ExpenseFilter;
import com.saranaresturantsystem.specification.purchases.Expenses.ExpenseSpec;
import com.saranaresturantsystem.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final ObjectMapper objectMapper;
    private final BackTransactionRepository bankTransactionRepository;

    @Transactional(readOnly = true)
    @Override
    public Page<ExpenseResponse> findAll(Map<String, String> params) {
        ExpenseFilter filter = objectMapper.convertValue(params, ExpenseFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Expenses> spec = ExpenseSpec.filterBy(filter);
        return expenseRepository.findAll(spec, pageable).map(expenseMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Expenses findById(Long id) {
        Expenses expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
        if ("INACTIVE".equalsIgnoreCase(expense.getStatus())) {
            throw new ResourceNotFoundException("Expense not found with id: " + id);
        }
        return expense;
    }

    @Override
    @Transactional
    public ExpenseResponse save(ExpenseRequest request) {
        // ១. Save Expense ធម្មតា
        Expenses expense = expenseMapper.toEntity(request);
        expense.setStatus("ACTIVE");
        Expenses savedExpense = expenseRepository.save(expense);

        // ២. 🔄 Insert ចូល tbl_bank_transactions ព្រមទាំងភ្ជាប់ expense_id
        Bank_Transactions bankTransaction = new Bank_Transactions();
        bankTransaction.setExpenseId(savedExpense.getId()); // 👈 កំណត់ expenseId ទីនេះ
        bankTransaction.setAmount(savedExpense.getAmount());
        bankTransaction.setTransactionReference(savedExpense.getReference());
        bankTransaction.setTransactionType("PURCHASE");
        bankTransaction.setStatus("ACTIVE");
        bankTransaction.setTransactionDate(LocalDateTime.now());
        bankTransaction.setDescription(savedExpense.getDescription() != null ? savedExpense.getDescription() : "Expense payment");

        bankTransactionRepository.save(bankTransaction);

        return expenseMapper.toResponse(savedExpense);
    }

    @Override
    @Transactional
    public ExpenseResponse update(Long id, ExpenseRequest request) {
        Expenses expense = findById(id);
        expenseMapper.updateEntityFromRequest(request, expense);
        Expenses updatedExpense = expenseRepository.save(expense);
        return expenseMapper.toResponse(updatedExpense);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Expenses expense = findById(id);
        expense.setStatus("INACTIVE");
        expenseRepository.save(expense);
    }
}