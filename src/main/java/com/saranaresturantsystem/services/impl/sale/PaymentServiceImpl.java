package com.saranaresturantsystem.services.impl.sale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.common.UniqueChecker;
import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.dto.request.sales.PaymentRequest;
import com.saranaresturantsystem.dto.response.sales.PaymentResponse;
import com.saranaresturantsystem.entities.finances.Banks;
import com.saranaresturantsystem.entities.sales.Payment;
import com.saranaresturantsystem.entities.sales.Sales;
import com.saranaresturantsystem.entities.users.User;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.sale.PaymentMapper;
import com.saranaresturantsystem.repository.sales.PaymentRepository;
import com.saranaresturantsystem.repository.sales.SaleRepository;
import com.saranaresturantsystem.services.interfaces.finances.BankService;
import com.saranaresturantsystem.services.interfaces.sales.PaymentService;
import com.saranaresturantsystem.services.interfaces.sales.SaleService;
import com.saranaresturantsystem.services.interfaces.users.UserService;
import com.saranaresturantsystem.specification.payment.PaymentFilter;
import com.saranaresturantsystem.specification.payment.PaymentSpec;
import com.saranaresturantsystem.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final SaleRepository saleRepository;
    private final SaleService saleService;
    private final BankService bankService;
    private final UserService userService;
    private final PaymentMapper paymentMapper;
    private final ObjectMapper objectMapper;
    private final UniqueChecker uniqueChecker ;

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> findAll(Map<String, String> params) {
        PaymentFilter filter = objectMapper.convertValue(params, PaymentFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Payment> spec = PaymentSpec.filterBy(filter);
        return paymentRepository.findAll(spec, pageable).map(paymentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Payment findById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
        if (Constants.STATUS_DELETE.equalsIgnoreCase(payment.getStatus())
                || Constants.STATUS_INIT.equalsIgnoreCase(payment.getStatus())) {
            throw new ResourceNotFoundException("Payment", id);
        }
        return payment;
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getById(Long id) {
        return paymentMapper.toResponse(findById(id));
    }

    @Override
    @Transactional
    public PaymentResponse create(PaymentRequest request) {
        Payment payment = paymentMapper.toEntity(request);

        Sales sale = saleService.findById(request.saleId());
        payment.setSales(sale);
        payment.setPaymentNo(generatePaymentNo());
        payment.setTransactionNo(generatePaymentNo());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(Constants.PAID);
        Banks bank = bankService.getBankById(request.bankId());
        payment.setBanks(bank);

        if (request.userId() != null) {
            User user = userService.findById(request.userId());
            payment.setUser(user);
        } else {
            try {
                payment.setUser(userService.getCurrentUser());
            } catch (Exception e) {
                log.debug("No authenticated user found: {}", e.getMessage());
            }
        }

        Payment savedPayment = paymentRepository.save(payment);
        recalculateSalePaymentStatus(sale);

        return paymentMapper.toResponse(savedPayment);
    }

    @Override
    @Transactional
    public PaymentResponse update(PaymentRequest request, Long id) {
        Payment payment = findById(id);
        Sales sale = payment.getSales();
        payment.setAmount(request.amount());
        bankService.getBankById(request.bankId());

        if (request.userId() != null) {
            User user = userService.findById(request.userId());
            payment.setUser(user);
        } else {
            try {
                User currentUser = userService.getCurrentUser();
                if (currentUser != null) {
                    payment.setUser(currentUser);
                }
            } catch (Exception e) {
                log.debug("No authenticated user found while updating payment: {}", e.getMessage());
            }
        }
        payment.setStatus(Constants.PAID);
        Payment savedPayment = paymentRepository.save(payment);
        recalculateSalePaymentStatus(sale);
        return paymentMapper.toResponse(savedPayment);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Payment payment = findById(id);
        payment.setStatus(Constants.STATUS_DELETE);
        paymentRepository.save(payment);

        if (payment.getSales() != null) {
            recalculateSalePaymentStatus(payment.getSales());
        }
    }

    private String generatePaymentNo() {
        Long maxId = paymentRepository.findMaxId();
        long nextSeq = (maxId == null ? 0L : maxId) + 1;
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("PAY-%s-%04d", dateStr, nextSeq);
    }

    private void recalculateSalePaymentStatus(Sales sale) {
        List<Payment> activePayments = paymentRepository.findBySalesIdAndStatus(sale.getId(), Constants.STATUS_ACTIVE);
        BigDecimal totalPaid = activePayments.stream()
                .map(Payment::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        sale.setPaidAmount(totalPaid.doubleValue());
        BigDecimal grandTotal = BigDecimal.valueOf(sale.getGrandTotal() == null ? 0D : sale.getGrandTotal());
        sale.setReturnAmount(totalPaid.subtract(grandTotal).max(BigDecimal.ZERO).doubleValue());

        if (totalPaid.signum() == 0) {
            sale.setPaymentStatus(Constants.PENDING);
        } else if (totalPaid.compareTo(grandTotal) < 0) {
            sale.setPaymentStatus(Constants.PARTIAL);
        } else {
            sale.setPaymentStatus(Constants.PAID);
        }
        saleRepository.save(sale);
    }
}

