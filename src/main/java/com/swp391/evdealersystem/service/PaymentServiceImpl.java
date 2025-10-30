package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.config.VnPayConfig;
import com.swp391.evdealersystem.entity.Order;
import com.swp391.evdealersystem.entity.Payment;
import com.swp391.evdealersystem.enums.OrderPaymentStatus;
import com.swp391.evdealersystem.enums.PaymentMethod;
import com.swp391.evdealersystem.enums.PaymentPurpose;
import com.swp391.evdealersystem.enums.PaymentStatus;
import com.swp391.evdealersystem.repository.OrderRepository;
import com.swp391.evdealersystem.repository.PaymentRepository;
import com.swp391.evdealersystem.util.VnPayUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final VnPayConfig cfg;
    private final OrderRepository orderRepo;
    private final PaymentRepository paymentRepo;

    private static final DateTimeFormatter VNP_TS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    // ======================== CREATE VNPAY PAYMENT ========================
    @Transactional
    @Override
    public Payment createVnPayPayment(Long orderId, PaymentPurpose purpose, BigDecimal amount, String clientIp) {
        Order order = orderRepo.findGraphByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        // Validate amount theo rule "1 lần cọc + 1 lần trả nốt"
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
        if (purpose == PaymentPurpose.DEPOSIT) {
            if (order.getDepositAmount() == null || amount.compareTo(order.getDepositAmount()) != 0) {
                throw new IllegalArgumentException("Deposit amount must equal order.depositAmount");
            }
        } else if (purpose == PaymentPurpose.BALANCE) {
            if (order.getRemainingAmount() == null || amount.compareTo(order.getRemainingAmount()) != 0) {
                throw new IllegalArgumentException("Balance amount must equal order.remainingAmount");
            }
        } else {
            throw new IllegalArgumentException("Unsupported purpose");
        }

        // Build VNPay params
        String vnp_TxnRef = UUID.randomUUID().toString().replace("-", "").substring(0, 20); // unique <= 20 chars
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", "2.1.0"); // hoặc lấy từ cfg nếu bạn thêm field version
        params.put("vnp_Command", "pay");   // hoặc lấy từ cfg nếu bạn thêm field command
        params.put("vnp_TmnCode", cfg.getTmnCode());
        params.put("vnp_Amount", amount.multiply(BigDecimal.valueOf(100)).toBigInteger().toString()); // x100
        params.put("vnp_CurrCode", cfg.getCurrCode());
        params.put("vnp_TxnRef", vnp_TxnRef);
        params.put("vnp_OrderInfo", purpose + " for order " + orderId);
        params.put("vnp_OrderType", cfg.getOrderType()); // ví dụ "other"
        params.put("vnp_Locale", cfg.getLocale());
        params.put("vnp_ReturnUrl", cfg.getReturnUrl());
        params.put("vnp_IpAddr", clientIp != null ? clientIp : "127.0.0.1");

        // create / expire theo giờ VN (+07:00)
        String create = now.withOffsetSameInstant(ZoneOffset.of("+07:00")).format(VNP_TS);
        String expire = now.plusMinutes(15).withOffsetSameInstant(ZoneOffset.of("+07:00")).format(VNP_TS);
        params.put("vnp_CreateDate", create);
        params.put("vnp_ExpireDate", expire);

        Map.Entry<String, String> pair = VnPayUtil.buildSignedQuery(params, cfg.getHashSecret());
        String payUrl = cfg.getPayUrl() + "?" + pair.getKey() + "&vnp_SecureHash=" + pair.getValue();

        // Persist Payment (PENDING)
        Payment payment = Payment.builder()
                .order(order)
                .amount(amount)
                .purpose(purpose)
                .paymentMethod(PaymentMethod.VNPAY)
                .status(PaymentStatus.PENDING)
                .vnpTxnRef(vnp_TxnRef)
                .payUrl(payUrl)
                .createdAt(now)
                .build();

        return paymentRepo.save(payment);
    }

    // ======================== CREATE CASH PAYMENT ========================
    @Transactional
    @Override
    public Payment createCashPayment(Long orderId, PaymentPurpose purpose, BigDecimal amount) {
        Order order = orderRepo.findGraphByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
        if (purpose == PaymentPurpose.DEPOSIT) {
            if (order.getDepositAmount() == null || amount.compareTo(order.getDepositAmount()) != 0) {
                throw new IllegalArgumentException("Deposit amount must equal order.depositAmount");
            }
        } else if (purpose == PaymentPurpose.BALANCE) {
            if (order.getRemainingAmount() == null || amount.compareTo(order.getRemainingAmount()) != 0) {
                throw new IllegalArgumentException("Balance amount must equal order.remainingAmount");
            }
        } else {
            throw new IllegalArgumentException("Unsupported purpose");
        }

        Payment p = Payment.builder()
                .order(order)
                .amount(amount)
                .purpose(purpose)
                .paymentMethod(PaymentMethod.CASH)
                .status(PaymentStatus.PAID)
                .createdAt(OffsetDateTime.now())
                .paidAt(OffsetDateTime.now())
                .build();

        // Cập nhật trạng thái của Order (không ghi thời gian lên Order)
        if (purpose == PaymentPurpose.DEPOSIT) {
            order.setPaymentStatus(OrderPaymentStatus.DEPOSIT_PAID);
        } else {
            order.setPaymentStatus(OrderPaymentStatus.PAID);
        }

        paymentRepo.save(p);
        orderRepo.save(order); // @PreUpdate Order sẽ sync status (COMPLETED/CANCELED/PROCESSING)
        return p;
    }

    // ======================== VNPay RETURN / IPN ========================
    @Transactional
    @Override
    public String handleReturn(Map<String, String> allParams) {
        boolean ok = verifyAndUpdate(allParams);
        return ok ? "Thanh toán thành công" : "Thanh toán thất bại";
    }

    @Transactional
    @Override
    public String handleIpn(Map<String, String> allParams) {
        boolean ok = verifyAndUpdate(allParams);
        return ok ? "OK" : "INVALID";
    }

    // ======================== VERIFY & UPDATE CORE ========================
    private boolean verifyAndUpdate(Map<String, String> allParams) {
        // 1) Verify secure hash
        String receivedHash = allParams.get("vnp_SecureHash");
        Map<String, String> signParams = new HashMap<>(allParams);
        signParams.remove("vnp_SecureHash");
        signParams.remove("vnp_SecureHashType");

        Map.Entry<String, String> pair = VnPayUtil.buildSignedQuery(signParams, cfg.getHashSecret());
        String calcHash = pair.getValue();
        if (receivedHash == null || !receivedHash.equalsIgnoreCase(calcHash)) {
            return false; // chữ ký không hợp lệ
        }

        // 2) Extract params
        String vnp_TxnRef = allParams.get("vnp_TxnRef");
        String vnp_ResponseCode = allParams.get("vnp_ResponseCode");
        String vnp_TransactionStatus = allParams.get("vnp_TransactionStatus");
        String vnp_TransactionNo = allParams.get("vnp_TransactionNo");
        String vnp_BankCode = allParams.get("vnp_BankCode");
        String vnp_BankTranNo = allParams.get("vnp_BankTranNo");

        Payment payment = paymentRepo.findByVnpTxnRef(vnp_TxnRef).orElse(null);
        if (payment == null) return false;

        // Idempotent
        if (payment.getStatus() == PaymentStatus.PAID || payment.getStatus() == PaymentStatus.FAILED) {
            return true;
        }

        // 3) VNPay success (both "00")
        boolean success = "00".equals(vnp_ResponseCode) && "00".equals(vnp_TransactionStatus);

        // Ghi thông tin phản hồi
        payment.setVnpResponseCode(vnp_ResponseCode);
        payment.setVnpTransactionStatus(vnp_TransactionStatus);
        payment.setVnpTransactionNo(vnp_TransactionNo);
        payment.setBankCode(vnp_BankCode);
        payment.setBankTranNo(vnp_BankTranNo);

        if (success) {
            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidAt(OffsetDateTime.now());

            Order order = payment.getOrder();
            if (payment.getPurpose() == PaymentPurpose.DEPOSIT) {
                // phải khớp đúng depositAmount
                if (order.getDepositAmount() != null
                        && payment.getAmount().compareTo(order.getDepositAmount()) == 0) {
                    order.setPaymentStatus(OrderPaymentStatus.DEPOSIT_PAID);
                } else {
                    // số tiền không khớp => fail
                    payment.setStatus(PaymentStatus.FAILED);
                }
            } else if (payment.getPurpose() == PaymentPurpose.BALANCE) {
                // phải khớp đúng remainingAmount tại thời điểm thanh toán
                if (order.getRemainingAmount() != null
                        && payment.getAmount().compareTo(order.getRemainingAmount()) == 0) {
                    order.setPaymentStatus(OrderPaymentStatus.PAID);
                } else {
                    payment.setStatus(PaymentStatus.FAILED);
                }
            }

            paymentRepo.save(payment);
            orderRepo.save(order);
            return payment.getStatus() == PaymentStatus.PAID;

        } else {
            // << FIXED: luôn set FAILED khi VNPay báo lỗi >>
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepo.save(payment);
            return false;
        }
    }
}
