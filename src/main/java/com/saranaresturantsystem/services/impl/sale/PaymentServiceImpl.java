package com.saranaresturantsystem.services.impl.sale;

import com.saranaresturantsystem.dto.request.sales.payment.PaymentLineRequestDTO;
import com.saranaresturantsystem.dto.request.sales.payment.PaymentRequestDTO;
import com.saranaresturantsystem.dto.response.payment.PaymentLineResponseDTO;
import com.saranaresturantsystem.dto.response.payment.PaymentResponseDTO;
import com.saranaresturantsystem.entities.finances.Banks;
import com.saranaresturantsystem.entities.finances.Currency;
import com.saranaresturantsystem.entities.purchase.Purchase;
import com.saranaresturantsystem.entities.sales.Payment;
import com.saranaresturantsystem.entities.sales.PaymentLine;
import com.saranaresturantsystem.entities.sales.Sales;
import com.saranaresturantsystem.entities.users.User;
import com.saranaresturantsystem.repository.finances.BankRepository;
import com.saranaresturantsystem.repository.finances.CurrencyRepository;
import com.saranaresturantsystem.repository.purchases.PurchasesRepository;
import com.saranaresturantsystem.repository.sales.PaymentRepository;
import com.saranaresturantsystem.repository.sales.SaleRepository;
import com.saranaresturantsystem.repository.users.UserRepository;
import com.saranaresturantsystem.services.interfaces.sales.PaymentService;
import com.saranaresturantsystem.specification.payment.PaymentFilter;
import com.saranaresturantsystem.specification.payment.PaymentSpec;
import com.saranaresturantsystem.utils.PageUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final SaleRepository salesRepository;
    private final PurchasesRepository purchaseRepository;
    private final BankRepository banksRepository;
    private final CurrencyRepository currencyRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> findAll(Map<String, String> params) {
        PaymentFilter filter = objectMapper.convertValue(params, PaymentFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Payment> spec = PaymentSpec.filterBy(filter);
        return paymentRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        // ១. ការពារការបាញ់ duplicate តាម Idempotency Key
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            var existing = paymentRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existing.isPresent()) {
                return mapToResponse(existing.get());
            }
        }

        // ២. រៀបចំ Lines និងគណនា Total Received ដោយទាញ Rate/Operation ពី Currency
        List<PaymentLine> linesToSave = new ArrayList<>();
        BigDecimal totalReceived = BigDecimal.ZERO;

        for (PaymentLineRequestDTO lineDTO : request.getLines()) {
            Banks bank = null;
            if (lineDTO.getBankId() != null) {
                bank = banksRepository.findById(lineDTO.getBankId()).orElse(null);
            }

            // ទាញយក Entity Currency ពី Database
            Currency currency = null;
            if (lineDTO.getCurrencyId() != null) {
                currency = currencyRepository.findById(lineDTO.getCurrencyId())
                        .orElseThrow(() -> new EntityNotFoundException("Currency not found with id: " + lineDTO.getCurrencyId()));
            }

            // គណនាបម្លែងទឹកប្រាក់ទៅជា Base Currency តាមរយៈ rate និង operation របស់ Currency
            BigDecimal lineBaseAmount = convertToBaseAmount(lineDTO.getAmount(), currency);
            totalReceived = totalReceived.add(lineBaseAmount);

            // បង្កើត PaymentLine តាមទម្រង់ដើមរបស់អ្នក (គ្មាន Field បន្ថែម)
            PaymentLine line = PaymentLine.builder()
                    .paymentMethod(lineDTO.getPaymentMethod())
                    .amount(lineDTO.getAmount())
                    .transactionNo(lineDTO.getTransactionNo())
                    .bank(bank)
                    .currency(currency)
                    .build();

            linesToSave.add(line);
        }

        // ៣. គណនា Return Amount និង Due Amount
        BigDecimal totalBill = request.getTotalAmount();
        BigDecimal returnAmount = BigDecimal.ZERO;
        BigDecimal dueAmount = BigDecimal.ZERO;
        String status;

        if (totalReceived.compareTo(totalBill) >= 0) {
            returnAmount = totalReceived.subtract(totalBill);
            status = "COMPLETED";
        } else {
            dueAmount = totalBill.subtract(totalReceived);
            status = "PARTIAL";
        }

        // ៤. Resolve Nullable Foreign Keys
        Sales sale = null;
        if (request.getSaleId() != null) {
            sale = salesRepository.findById(request.getSaleId()).orElse(null);
        }

        Purchase purchase = null;
        if (request.getPurchaseId() != null) {
            purchase = purchaseRepository.findById(request.getPurchaseId()).orElse(null);
        }

        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId()).orElse(null);
        }

        // ៥. Build Master Payment
        Payment payment = Payment.builder()
                .paymentNo("PAY-" + System.currentTimeMillis())
                .idempotencyKey(request.getIdempotencyKey())
                .totalAmount(totalBill)
                .receivedAmount(totalReceived)
                .returnAmount(returnAmount)
                .dueAmount(dueAmount)
                .status(status)
                .paymentDate(LocalDateTime.now())
                .note(request.getNote())
                .sales(sale)
                .purchase(purchase)
                .user(user)
                .build();

        // ៦. ភ្ជាប់ lines ទៅ payment
        for (PaymentLine line : linesToSave) {
            payment.addPaymentLine(line);
        }

        Payment savedPayment = paymentRepository.save(payment);
        return mapToResponse(savedPayment);
    }

    // Helper method: បម្លែងទឹកប្រាក់តាម operation និង rate ដែលមានស្រាប់ក្នុង Currency
    private BigDecimal convertToBaseAmount(BigDecimal amount, Currency currency) {
        if (currency == null || currency.getRate() == 0) {
            return amount;
        }

        BigDecimal rate = BigDecimal.valueOf(currency.getRate());
        String operation = currency.getOperation();

        if ("/".equals(operation)) {
            // ឧទាហរណ៍៖ 41,000 KHR / 4100 = 10 USD
            return amount.divide(rate, 4, RoundingMode.HALF_UP);
        } else if ("*".equals(operation)) {
            // ឧទាហរណ៍៖ ករណីគុណ
            return amount.multiply(rate);
        }

        return amount;
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));
        return mapToResponse(payment);
    }

    private PaymentResponseDTO mapToResponse(Payment p) {
        return PaymentResponseDTO.builder()
                .id(p.getId())
                .paymentNo(p.getPaymentNo())
                .totalAmount(p.getTotalAmount())
                .receivedAmount(p.getReceivedAmount())
                .returnAmount(p.getReturnAmount())
                .dueAmount(p.getDueAmount())
                .status(p.getStatus())
                .paymentDate(p.getPaymentDate())
                .note(p.getNote())
                .saleId(p.getSales() != null ? p.getSales().getId() : null)
                .purchaseId(p.getPurchase() != null ? p.getPurchase().getId() : null)
                .userId(p.getUser() != null ? p.getUser().getId() : null)
                .username(p.getUser() != null ? p.getUser().getUsername() : null)
                .lines(p.getPaymentLines().stream().map(line -> PaymentLineResponseDTO.builder()
                        .id(line.getId())
                        .paymentMethod(line.getPaymentMethod())
                        .amount(line.getAmount())
                        .transactionNo(line.getTransactionNo())
                        .bankId(line.getBank() != null ? line.getBank().getId() : null)
                        .bankName(line.getBank() != null ? line.getBank().getName() : null)
                        .currencyId(line.getCurrency() != null ? line.getCurrency().getId() : null)
                        .currencyCode(line.getCurrency() != null ? line.getCurrency().getCode() : null)
                        .build()
                ).collect(Collectors.toList()))
                .build();
    }
}