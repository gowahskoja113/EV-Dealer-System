package com.swp391.evdealersystem.entity;


import com.swp391.evdealersystem.enums.PaymentMethod;
import com.swp391.evdealersystem.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "payment")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(length = 50, unique = true)
    private String vnpTxnRef; // mã tham chiếu của bạn gửi lên VNPAY

    @Column(length = 50)
    private String vnpTransactionNo; // mã giao dịch của VNPAY trả về (nếu có)

    @Column(length = 20)
    private String vnpResponseCode; // vnp_ResponseCode

    @Column(length = 20)
    private String vnpTransactionStatus; // vnp_TransactionStatus

    @Column(length = 20)
    private String bankCode; // vnp_BankCode

    @Column(length = 64)
    private String bankTranNo; // vnp_BankTranNo

    @Column(length = 2048)
    private String payUrl; // URL redirect thanh toán (đã ký)

    private OffsetDateTime createdAt; // khi tạo yêu cầu
    private OffsetDateTime paidAt; // khi thanh toán thành công

    @Column(length = 64)
    private String orderId; // liên kết đơn hàng nội bộ của bạn (nếu có)
}