package com.swp391.evdealersystem.entity;

import com.swp391.evdealersystem.enums.PaymentMethod;
import com.swp391.evdealersystem.enums.PaymentPurpose;
import com.swp391.evdealersystem.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "payment",
        indexes = {
                @Index(name = "idx_payment_order", columnList = "order_id"),
                @Index(name = "idx_payment_status", columnList = "status"),
                @Index(name = "idx_payment_txnref", columnList = "vnpTxnRef", unique = true)
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id",
            foreignKey = @ForeignKey(name = "fk_payment_order"))
    private Order order;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PaymentPurpose purpose; // DEPOSIT | BALANCE

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod paymentMethod; // VNPAY

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status; // PENDING|PAID|FAILED|CANCELED

    // VNPay fields
    @Column(length = 50, unique = true)
    private String vnpTxnRef;             // mã tham chiếu gửi lên VNPay

    @Column(length = 50)
    private String vnpTransactionNo;      // mã giao dịch VNPAY

    @Column(length = 20)
    private String vnpResponseCode;       // vnp_ResponseCode

    @Column(length = 20)
    private String vnpTransactionStatus;  // vnp_TransactionStatus

    @Column(length = 20)
    private String bankCode;              // vnp_BankCode

    @Column(length = 64)
    private String bankTranNo;            // vnp_BankTranNo

    @Column(length = 2048)
    private String payUrl;                // URL redirect thanh toán (đã ký)

    private OffsetDateTime createdAt;     // khi tạo yêu cầu
    private OffsetDateTime paidAt;        // khi thanh toán thành công
}
