package com.saranaresturantsystem.services.impl.sale;

import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.dto.request.sales.payment.PaymentLineRequestDTO;
import com.saranaresturantsystem.dto.request.sales.payment.PaymentRequestDTO;
import com.saranaresturantsystem.dto.response.payment.PaymentLineResponseDTO;
import com.saranaresturantsystem.dto.response.payment.PaymentResponseDTO;
import com.saranaresturantsystem.entities.finances.BankTransaction;
import com.saranaresturantsystem.entities.finances.Banks;
import com.saranaresturantsystem.entities.finances.Currency;
import com.saranaresturantsystem.entities.purchase.Purchase;
import com.saranaresturantsystem.entities.sales.Payment;
import com.saranaresturantsystem.entities.sales.PaymentLine;
import com.saranaresturantsystem.entities.sales.Sales;
import com.saranaresturantsystem.entities.users.User;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.repository.finances.BankRepository;
import com.saranaresturantsystem.repository.finances.BankTransactionRepository;
import com.saranaresturantsystem.repository.finances.CurrencyRepository;
import com.saranaresturantsystem.repository.purchases.PurchasesRepository;
import com.saranaresturantsystem.repository.sales.PaymentRepository;
import com.saranaresturantsystem.repository.sales.SaleRepository;
import com.saranaresturantsystem.repository.users.UserRepository;
import com.saranaresturantsystem.services.interfaces.sales.PaymentService;
import com.saranaresturantsystem.services.interfaces.sales.SaleService;
import com.saranaresturantsystem.specification.payment.PaymentFilter;
import com.saranaresturantsystem.specification.payment.PaymentSpec;
import com.saranaresturantsystem.utils.PageUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final SaleRepository salesRepository;
    private final SaleService saleService;
    private final PurchasesRepository purchaseRepository;
    private final BankRepository banksRepository;
    private final BankTransactionRepository bankTransactionRepository;
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
    @Caching(evict = {
            @CacheEvict(value = "sales", allEntries = true),
            @CacheEvict(value = "report:payments:sales", allEntries = true),
            @CacheEvict(value = "report:payments:purchases", allEntries = true),
            @CacheEvict(value = "report:sales", allEntries = true),
            @CacheEvict(value = "report:purchases", allEntries = true),
            @CacheEvict(value = "report:banks", allEntries = true),
            @CacheEvict(value = "report:profit-loss", allEntries = true)
    })
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        // ១. ការពារការបាញ់ duplicate តាម Idempotency Key
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            var existing = paymentRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existing.isPresent()) {
                Payment existingPayment = existing.get();
                String salePaymentStatus = synchronizeSale(existingPayment.getSales(), existingPayment);
                String purchasePaymentStatus = synchronizePurchase(existingPayment.getPurchase(), existingPayment);
                if (salePaymentStatus != null) {
                    existingPayment.setStatus(salePaymentStatus);
                    paymentRepository.save(existingPayment);
                } else if (purchasePaymentStatus != null) {
                    existingPayment.setStatus(purchasePaymentStatus);
                    paymentRepository.save(existingPayment);
                }
                return mapToResponse(existingPayment);
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
            if (bank == null && lineDTO.getPaymentMethod() != null && !"CASH".equalsIgnoreCase(lineDTO.getPaymentMethod())) {
                bank = banksRepository.findAll().stream()
                        .filter(b -> !Constants.STATUS_DELETE.equalsIgnoreCase(b.getStatus()))
                        .findFirst()
                        .orElse(null);
            }

            // ទាញយក Entity Currency ពី Database
            Currency currency = null;
            if (lineDTO.getCurrencyId() != null) {
                currency = currencyRepository.findById(lineDTO.getCurrencyId())
                        .orElseThrow(() -> new ResourceNotFoundException("Currency not found with id: " + lineDTO.getCurrencyId()));
            }

            // គណនាបម្លែងទឹកប្រាក់ទៅជា Base Currency តាមរយៈ rate និង operation របស់ Currency
            BigDecimal lineBaseAmount = convertToBaseAmount(lineDTO.getAmount(), currency);
            totalReceived = totalReceived.add(lineBaseAmount);

            // បង្កើត PaymentLine
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

        Payment savedPayment = paymentRepository.saveAndFlush(payment);

        // ៧. អាប់ដេតសមតុល្យធនាគារ (Bank Balance): បូកចូលសម្រាប់ Sale / ដកចេញសម្រាប់ Purchase
        boolean isPurchasePayment = (purchase != null || request.getPurchaseId() != null);
        Long saleIdVal = sale != null ? sale.getId() : request.getSaleId();
        Long purchaseIdVal = purchase != null ? purchase.getId() : request.getPurchaseId();

        int lineIdx = 1;
        for (PaymentLine line : savedPayment.getPaymentLines()) {
            Banks bank = line.getBank();
            if (bank != null) {
                BigDecimal lineBaseAmount = convertToBaseAmount(line.getAmount(), line.getCurrency());
                BigDecimal currentBal = bank.getCurrentBalance() != null
                        ? bank.getCurrentBalance()
                        : (bank.getOpeningBalance() != null ? bank.getOpeningBalance() : BigDecimal.ZERO);

                String txType;
                String description;

                if (isPurchasePayment) {
                    // Purchase តាម Bank: ដកប្រាក់ចេញពី Bank (-)
                    BigDecimal newBal = currentBal.subtract(lineBaseAmount);
                    bank.setCurrentBalance(newBal);
                    txType = Constants.PURCHASE;
                    description = "Purchase payment for " + (purchase != null && purchase.getReferenceNo() != null ? purchase.getReferenceNo() : ("Purchase #" + purchaseIdVal));
                    log.info("Deducted {} from Bank ID {} ({}). New Balance: {}", lineBaseAmount, bank.getId(), bank.getName(), newBal);
                } else {
                    // Sale គិតលុយតាម Bank: បូកប្រាក់ចូលទៅក្នុង Bank (+)
                    BigDecimal newBal = currentBal.add(lineBaseAmount);
                    bank.setCurrentBalance(newBal);
                    txType = Constants.SALE;
                    description = "Sale payment received for " + (sale != null && sale.getNo() != null ? sale.getNo() : ("Sale #" + saleIdVal));
                    log.info("Added {} to Bank ID {} ({}). New Balance: {}", lineBaseAmount, bank.getId(), bank.getName(), newBal);
                }
                banksRepository.save(bank);

                // កត់ត្រាចូល tbl_bank_transactions
                String uniqueSuffix = (line.getTransactionNo() != null && !line.getTransactionNo().isBlank())
                        ? line.getTransactionNo()
                        : ("L" + lineIdx + "-" + (System.nanoTime() % 1000000));
                String txRef = savedPayment.getPaymentNo() + "-" + uniqueSuffix;

                BankTransaction bt = new BankTransaction();
                bt.setBankId(bank.getId());
                bt.setSaleId(saleIdVal);
                bt.setPurchaseId(purchaseIdVal);
                bt.setAmount(lineBaseAmount);
                bt.setTransactionReference(txRef);
                bt.setTransactionType(txType);
                bt.setStatus(Constants.STATUS_ACTIVE);
                bt.setTransactionDate(LocalDateTime.now());
                bt.setDescription(description);
                bankTransactionRepository.save(bt);
            }
            lineIdx++;
        }

        // ៨. ធ្វើសមកាលកម្ម Sale និង Purchase Payment Status
        String salePaymentStatus = synchronizeSale(sale, savedPayment);
        String purchasePaymentStatus = synchronizePurchase(purchase, savedPayment);

        if (salePaymentStatus != null) {
            savedPayment.setStatus(salePaymentStatus);
            paymentRepository.save(savedPayment);
        } else if (purchasePaymentStatus != null) {
            savedPayment.setStatus(purchasePaymentStatus);
            paymentRepository.save(savedPayment);
        }

        return mapToResponse(savedPayment);
    }

    private String synchronizePurchase(Purchase purchase, Payment currentPayment) {
        if (purchase == null || purchase.getId() == null) {
            return null;
        }

        Purchase lockedPurchase = purchaseRepository.findById(purchase.getId())
                .orElse(purchase);
        BigDecimal grandTotal = lockedPurchase.getGrandTotal() != null
                ? lockedPurchase.getGrandTotal()
                : BigDecimal.ZERO;

        List<Payment> payments = paymentRepository.findByPurchaseId(lockedPurchase.getId());
        if (currentPayment != null && currentPayment.getReceivedAmount() != null) {
            boolean alreadyPresent = payments.stream()
                    .anyMatch(p -> p.getId() != null && p.getId().equals(currentPayment.getId()));
            if (!alreadyPresent) {
                payments = new ArrayList<>(payments);
                payments.add(currentPayment);
            }
        }

        BigDecimal paid = payments.stream()
                .map(Payment::getReceivedAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Fallback: If no payment records found, use lockedPurchase's existing paid amount if any
        if (paid.signum() == 0 && lockedPurchase.getPaidAmount() != null && lockedPurchase.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            paid = lockedPurchase.getPaidAmount();
        }

        BigDecimal appliedPaid = paid.min(grandTotal);
        BigDecimal due = grandTotal.subtract(appliedPaid).max(BigDecimal.ZERO);

        lockedPurchase.setPaidAmount(appliedPaid);
        lockedPurchase.setDueAmount(due);

        String paymentStatus;
        if (grandTotal.compareTo(BigDecimal.ZERO) <= 0 || (paid.signum() > 0 && paid.compareTo(grandTotal) >= 0)) {
            paymentStatus = Constants.PAID;
        } else if (paid.signum() == 0) {
            paymentStatus = Constants.PENDING;
        } else {
            paymentStatus = Constants.PARTIAL;
        }

        lockedPurchase.setPaymentStatus(paymentStatus);
        purchaseRepository.save(lockedPurchase);
        log.info("Purchase ID {} synchronized: Paid={}, Due={}, Status={}", lockedPurchase.getId(), appliedPaid, due, paymentStatus);
        return paymentStatus;
    }

    private String synchronizeSale(Sales sale, Payment currentPayment) {
        if (sale == null || sale.getId() == null) {
            return null;
        }

        Sales lockedSale = salesRepository.findByIdWithLock(sale.getId())
                .orElseThrow(() -> new EntityNotFoundException("Sale not found with id: " + sale.getId()));
        BigDecimal grandTotal = lockedSale.getGrandTotal() != null
                ? BigDecimal.valueOf(lockedSale.getGrandTotal())
                : BigDecimal.ZERO;

        List<Payment> payments = paymentRepository.findBySalesId(lockedSale.getId());
        if (currentPayment != null && currentPayment.getReceivedAmount() != null) {
            boolean alreadyPresent = payments.stream()
                    .anyMatch(p -> p.getId() != null && p.getId().equals(currentPayment.getId()));
            if (!alreadyPresent) {
                payments = new ArrayList<>(payments);
                payments.add(currentPayment);
            }
        }

        BigDecimal paid = payments.stream()
                .map(Payment::getReceivedAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Fallback: If no payment records found, use lockedSale's existing paid amount if any
        if (paid.signum() == 0 && lockedSale.getPaidAmount() != null && lockedSale.getPaidAmount() > 0) {
            paid = BigDecimal.valueOf(lockedSale.getPaidAmount());
        }

        BigDecimal appliedPaid = paid.min(grandTotal);
        lockedSale.setPaidAmount(appliedPaid.doubleValue());
        lockedSale.setReturnAmount(paid.subtract(grandTotal).max(BigDecimal.ZERO).doubleValue());

        if (grandTotal.compareTo(BigDecimal.ZERO) <= 0 || (paid.signum() > 0 && paid.compareTo(grandTotal) >= 0)) {
            lockedSale.setPaymentStatus("PAID");
            saleService.complete(lockedSale.getId(), "system");
            lockedSale.setSaleStatus("COMPLETED");
        } else if (paid.signum() == 0) {
            lockedSale.setPaymentStatus("PENDING");
        } else {
            lockedSale.setPaymentStatus("PARTIAL");
        }

        salesRepository.save(lockedSale);
        return lockedSale.getPaymentStatus();
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
 