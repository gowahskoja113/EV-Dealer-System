package com.swp391.evdealersystem.entity;

import com.swp391.evdealersystem.enums.OrderPaymentStatus;
import com.swp391.evdealersystem.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "orders",
        indexes = {
                @Index(name = "idx_orders_customer", columnList = "customer_id"),
                @Index(name = "idx_orders_vehicle",  columnList = "vehicle_id"),
                @Index(name = "idx_orders_status",   columnList = "status"),
                @Index(name = "idx_orders_payment_status", columnList = "payment_status"),
                @Index(name = "idx_orders_order_date",     columnList = "order_date")
        })
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_customer"))
    private Customer customer;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_vehicle"))
    private ElectricVehicle vehicle; // LƯU Ý: vehicle phải có field price (BigDecimal)

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = true, length = 20)
    private OrderStatus status; // PROCESSING -> COMPLETED / CANCELED

    @Column(name = "deposit_amount", precision = 18, scale = 2)
    private BigDecimal depositAmount; // có thể null/0

    // Số tiền còn lại phải thanh toán sau khi trừ cọc
    @Column(name = "remaining_amount", precision = 18, scale = 2, nullable = true)
    private BigDecimal remainingAmount;

    // Trạng thái thanh toán của PHẦN CÒN LẠI
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 20, nullable = true)
    private OrderPaymentStatus paymentStatus; // UNPAID | PARTIAL | PAID | OVERDUE (ví dụ)

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(name = "currency", length = 8, nullable = true)
    private String currency;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    // ===================== Lifecycle =====================

    @PrePersist
    void prePersist() {
        if (orderDate == null) orderDate = LocalDateTime.now();
        if (currency == null)  currency = "VND";
        if (depositAmount == null) depositAmount = BigDecimal.ZERO;

        BigDecimal price = (vehicle != null && vehicle.getPrice() != null) ? vehicle.getPrice() : BigDecimal.ZERO;
        remainingAmount = maxZero(price.subtract(depositAmount));

        if (status == null) status = OrderStatus.PROCESSING;

        // Đừng auto set PAID theo remainingAmount; để UNPAID mặc định
        if (paymentStatus == null) {
            paymentStatus = OrderPaymentStatus.UNPAID;
        }

        syncOrderStatusFromPayment();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        // đảm bảo remainingAmount không bị sai lệch nếu depositAmount hoặc vehicle.price đổi
        BigDecimal price = (vehicle != null && vehicle.getPrice() != null)
                ? vehicle.getPrice()
                : BigDecimal.ZERO;

        if (depositAmount == null) depositAmount = BigDecimal.ZERO;
        remainingAmount = maxZero(price.subtract(depositAmount));

        // nếu remaining = 0, ép paymentStatus = PAID
        if (remainingAmount.signum() == 0 && paymentStatus != OrderPaymentStatus.PAID) {
            paymentStatus = OrderPaymentStatus.PAID;
        }

        syncOrderStatusFromPayment();
        updatedAt = LocalDateTime.now();
    }

    private void syncOrderStatusFromPayment() {
        // Rule:
        // - paymentStatus PAID  -> COMPLETED
        // - paymentStatus OVERDUE -> CANCELED
        // - còn lại              -> PROCESSING
        if (paymentStatus == OrderPaymentStatus.PAID) {
            status = OrderStatus.COMPLETED;
        } else if (paymentStatus == OrderPaymentStatus.OVERDUE) {
            status = OrderStatus.CANCELED;
        } else {
            status = OrderStatus.PROCESSING;
        }
    }

    private static BigDecimal maxZero(BigDecimal v) {
        return v.signum() < 0 ? BigDecimal.ZERO : v;
    }
}
