package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.config.PaymentConfig;
import com.swp391.evdealersystem.dto.request.PaymentRequest;
import com.swp391.evdealersystem.dto.response.PaymentResponse;
import com.swp391.evdealersystem.entity.Payment;
import com.swp391.evdealersystem.enums.PaymentStatus;
import com.swp391.evdealersystem.mapper.PaymentMapper;
import com.swp391.evdealersystem.repository.PaymentRepository;
import com.swp391.evdealersystem.util.PaymentUtil;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

public class PaymentServiceImpl implements PaymentService{
    private final PaymentConfig paymentConfig;
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final PaymentUtil paymentUtil;

    public PaymentServiceImpl(PaymentConfig paymentConfig, PaymentMapper paymentMapper, PaymentRepository paymentRepository, PaymentUtil paymentUtil) {
        this.paymentConfig = paymentConfig;
        this.paymentMapper = paymentMapper;
        this.paymentRepository = paymentRepository;
        this.paymentUtil = paymentUtil;
    }

    @Override
    @Transactional
    public PaymentResponse createVnPayPayment(PaymentRequest req, String clientIp) {
        String vnpTxnRef = String.valueOf(System.currentTimeMillis());
        long amountVND = req.getAmount().multiply(BigDecimal.valueOf(100)).longValue(); // VNPAY yêu cầu x100

        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", paymentConfig.getVersion());
        params.put("vnp_Command", paymentConfig.getCommand());
        params.put("vnp_TmnCode", paymentConfig.getTmnCode());
        params.put("vnp_Amount", String.valueOf(amountVND));
        params.put("vnp_CurrCode", paymentConfig.getCurrCode());
        params.put("vnp_TxnRef", vnpTxnRef);
        params.put("vnp_OrderInfo", "Thanh toan don hang " + (req.getOrderId() != null ? req.getOrderId() : vnpTxnRef));
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", paymentConfig.getLocale());
        params.put("vnp_ReturnUrl", paymentConfig.getReturnUrl());
        params.put("vnp_IpAddr", clientIp);
        params.put("vnp_CreateDate", paymentUtil.dateStringVNPay(new Date()));
        if (req.getBankCode() != null && !req.getBankCode().isBlank()) params.put("vnp_BankCode", req.getBankCode());

        String hashData = paymentUtil.hashAllFields(params);
        String secureHash = paymentUtil.hmacSHA512(paymentConfig.getHashSecret(), hashData);
        params.put("vnp_SecureHash", secureHash);

        String payUrl = paymentConfig.getPayUrl() + "?" + paymentUtil.buildQuery(params);

        Payment payment = paymentMapper.toEntity(req, vnpTxnRef, payUrl);
        paymentRepository.save(payment);
        return paymentMapper.toDTO(payment);
    }

    @Override
    @Transactional
    public PaymentResponse handleReturn(Map<String, String> vnpParams) {

        String receivedHash = vnpParams.remove("vnp_SecureHash");
        vnpParams.remove("vnp_SecureHashType");
        String signedData = paymentUtil.hashAllFields(vnpParams);
        String calcHash = paymentUtil.hmacSHA512(paymentConfig.getHashSecret(), signedData);
        if (!Objects.equals(receivedHash, calcHash)) {
            throw new IllegalArgumentException("Invalid VNPAY signature");
        }

        String vnpTxnRef = vnpParams.get("vnp_TxnRef");
        Payment payment = paymentRepository.findByVnpTxnRef(vnpTxnRef)
                .orElseThrow(() -> new NoSuchElementException("Payment not found"));

        String rspCode = vnpParams.get("vnp_ResponseCode");
        String transStatus = vnpParams.get("vnp_TransactionStatus");

        payment.setVnpResponseCode(rspCode);
        payment.setVnpTransactionStatus(transStatus);
        payment.setVnpTransactionNo(vnpParams.get("vnp_TransactionNo"));
        payment.setBankCode(vnpParams.get("vnp_BankCode"));
        payment.setBankTranNo(vnpParams.get("vnp_BankTranNo"));

        if ("00".equals(rspCode) && "00".equals(transStatus)) {
            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidAt(OffsetDateTime.now());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }
        paymentRepository.save(payment);
        return paymentMapper.toDTO(payment);
    }

    @Override
    @Transactional
    public String handleIpn(Map<String, String> vnpParams) {
// Similar to return, but respond in VNPAY format
        String receivedHash = vnpParams.remove("vnp_SecureHash");
        vnpParams.remove("vnp_SecureHashType");
        String signedData = paymentUtil.hashAllFields(vnpParams);
        String calcHash = paymentUtil.hmacSHA512(paymentConfig.getHashSecret(), signedData);
        if (!Objects.equals(receivedHash, calcHash)) {
            return "{" + "\"RspCode\":\"97\",\"Message\":\"Invalid signature\"}";
        }
        String vnpTxnRef = vnpParams.get("vnp_TxnRef");
        Optional<Payment> op = paymentRepository.findByVnpTxnRef(vnpTxnRef);
        if (op.isEmpty()) {
            return "{" + "\"RspCode\":\"01\",\"Message\":\"Order not found\"}";
        }
        Payment payment = op.get();
        String rspCode = vnpParams.get("vnp_ResponseCode");
        String transStatus = vnpParams.get("vnp_TransactionStatus");
        payment.setVnpResponseCode(rspCode);
        payment.setVnpTransactionStatus(transStatus);
        payment.setVnpTransactionNo(vnpParams.get("vnp_TransactionNo"));
        payment.setBankCode(vnpParams.get("vnp_BankCode"));
        payment.setBankTranNo(vnpParams.get("vnp_BankTranNo"));
        if ("00".equals(rspCode) && "00".equals(transStatus)) {
            payment.setStatus(PaymentStatus.PAID);
            if (payment.getPaidAt() == null) payment.setPaidAt(OffsetDateTime.now());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }
        paymentRepository.save(payment);
        return "{" + "\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}";
    }

    @Override
    public PaymentResponse getById(Long id) {
        return paymentMapper.toDTO(
                paymentRepository.findById(id).orElse(null)
        );
    }
}
