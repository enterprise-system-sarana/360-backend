package com.saranaresturantsystem.services.impl.sale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.common.InvoiceNumberService;
import com.saranaresturantsystem.dto.request.sales.CloseCashRegisterRequest;
import com.saranaresturantsystem.dto.request.sales.OpenCashRegisterRequest;
import com.saranaresturantsystem.dto.response.sales.CashRegisterResponse;
import com.saranaresturantsystem.entities.sales.CashRegister;
import com.saranaresturantsystem.entities.users.User;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.sale.CashRegisterMapper;
import com.saranaresturantsystem.repository.sales.CashRegisterRepository;
import com.saranaresturantsystem.repository.sales.SaleRepository;
import com.saranaresturantsystem.services.interfaces.inventory.StoreService;
import com.saranaresturantsystem.services.interfaces.sales.CashRegisterService;
import com.saranaresturantsystem.services.interfaces.users.UserService;
import com.saranaresturantsystem.specification.sales.CashRegisterFilter;
import com.saranaresturantsystem.specification.sales.CashRegisterSpec;
import com.saranaresturantsystem.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import static com.saranaresturantsystem.constants.Constants.*;

@Service
@RequiredArgsConstructor
public class CashRegisterServiceImpl implements CashRegisterService {

    private static final ZoneId PHNOM_PENH = ZoneId.of("Asia/Phnom_Penh");

    private final CashRegisterRepository cashRegisterRepository;
    private final SaleRepository saleRepository;
    private final CashRegisterMapper cashRegisterMapper;
    private final ObjectMapper objectMapper;
    private final InvoiceNumberService invoiceNumberService;
    private final StoreService storeService;
    private final UserService userService;

    @Override
    @Transactional
    public CashRegisterResponse openRegister(OpenCashRegisterRequest request, String createdBy) {
        User user = currentUser(createdBy);
        if (user == null) {
            throw new IllegalStateException("Cannot open cash register without an authenticated user");
        }

        // Check if user already has an open register
        if (cashRegisterRepository.existsOpenRegisterByUserId(user.getId())) {
            throw new IllegalStateException("User already has an open cash register. Please close it before opening a new one.");
        }

        var store = storeService.findById(request.storeId());

        CashRegister register = new CashRegister();
        register.setRegisterNo(invoiceNumberService.generate("REG"));
        register.setStore(store);
        register.setUser(user);
        register.setOpeningBalance(request.openingBalance());
        register.setTotalSales(BigDecimal.ZERO);
        register.setTotalCashSales(BigDecimal.ZERO);
        register.setTotalBankSales(BigDecimal.ZERO);
        register.setTotalReturns(BigDecimal.ZERO);
        register.setTotalCashIn(BigDecimal.ZERO);
        register.setTotalCashOut(BigDecimal.ZERO);
        register.setSaleCount(0);
        register.setStatus(REGISTER_OPEN);
        register.setOpenedAt(LocalDateTime.now(PHNOM_PENH));
        register.setCreatedBy(createdBy);

        return cashRegisterMapper.toResponse(cashRegisterRepository.save(register));
    }

    @Override
    @Transactional
    public CashRegisterResponse closeRegister(Long id, CloseCashRegisterRequest request, String updatedBy) {
        CashRegister register = findEntityById(id);

        if (!REGISTER_OPEN.equals(register.getStatus())) {
            throw new IllegalStateException("Only OPEN cash registers can be closed");
        }

        // Calculate expected balance = openingBalance + (totalCashSales != null ? totalCashSales : totalSales) - totalReturns + totalCashIn - totalCashOut
        BigDecimal totalSales = register.getTotalSales() != null ? register.getTotalSales() : BigDecimal.ZERO;
        BigDecimal totalCashSales = register.getTotalCashSales();
        BigDecimal cashSalesToAdd = totalCashSales != null ? totalCashSales : totalSales;
        BigDecimal totalReturns = register.getTotalReturns() != null ? register.getTotalReturns() : BigDecimal.ZERO;
        BigDecimal totalCashIn = register.getTotalCashIn() != null ? register.getTotalCashIn() : BigDecimal.ZERO;
        BigDecimal totalCashOut = register.getTotalCashOut() != null ? register.getTotalCashOut() : BigDecimal.ZERO;

        BigDecimal expectedBalance = register.getOpeningBalance()
                .add(cashSalesToAdd)
                .subtract(totalReturns)
                .add(totalCashIn)
                .subtract(totalCashOut);

        register.setExpectedBalance(expectedBalance);
        register.setClosingBalance(request.closingBalance());
        register.setDifference(request.closingBalance().subtract(expectedBalance));
        register.setClosingNote(request.closingNote());
        register.setStatus(REGISTER_CLOSED);
        register.setClosedAt(LocalDateTime.now(PHNOM_PENH));
        register.setUpdatedBy(updatedBy);

        return cashRegisterMapper.toResponse(cashRegisterRepository.save(register));
    }

    @Override
    @Transactional(readOnly = true)
    public CashRegisterResponse getById(Long id) {
        return cashRegisterMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CashRegisterResponse getCurrentOpen() {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        return cashRegisterRepository.findOpenRegisterByUserId(user.getId())
                .map(cashRegisterMapper::toResponse)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CashRegisterResponse> getAll(Map<String, String> params) {
        CashRegisterFilter filter = objectMapper.convertValue(params, CashRegisterFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<CashRegister> spec = CashRegisterSpec.filter(filter);
        return cashRegisterRepository.findAll(spec, pageable).map(cashRegisterMapper::toResponse);
    }

    private CashRegister findEntityById(Long id) {
        return cashRegisterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cash Register", id));
    }

    private User currentUser(String actor) {
        if (actor == null || "system".equalsIgnoreCase(actor)) {
            return null;
        }
        return userService.getCurrentUser();
    }
}
