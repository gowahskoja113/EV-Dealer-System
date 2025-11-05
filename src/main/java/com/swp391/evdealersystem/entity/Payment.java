package com.swp391.evdealersystem.entity;

import com.swp391.evdealersystem.enums.PaymentMethod;
import com.swp391.evdealersystem.enums.PaymentPurpose;
import com.swp391.evdealersystem.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity @Table(name="payment")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PaymentStatus status;     // PENDING, SUCCESS, FAILED

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PaymentPurpose type;         // DEPOSIT or REMAINING

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PaymentMethod method;     // CASH, VNPAY

    @Column(length = 50, unique = false)
    private String transactionRef;    // với CASH có thể null

    private LocalDateTime paymentDate;

    @Column(length = 255)
    private String message;
}