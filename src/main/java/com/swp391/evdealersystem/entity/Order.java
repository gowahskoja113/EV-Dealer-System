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
                @Index(name = "idx_orders_serial", columnList = "vehicle_serial_id"),
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

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = true, length = 20)
    private OrderStatus status;

    @Column(name = "deposit_amount", precision = 18, scale = 2)
    private BigDecimal depositAmount;

    // total - deposit
    @Column(name = "remaining_amount", precision = 18, scale = 2, nullable = true)
    private BigDecimal remainingAmount;

    @Column(name = "planned_deposit_amount", precision = 18, scale = 2)
    private BigDecimal plannedDepositAmount;

    // remaining status
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 20, nullable = true)
    private OrderPaymentStatus paymentStatus;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(name = "currency", length = 8, nullable = true)
    private String currency;

    @Column(name = "deposit_paid_at")
    private LocalDateTime depositPaidAt;

    @Column(name = "fully_paid_at")
    private LocalDateTime fullyPaidAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_serial_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_vehicle_serial"))
    private VehicleSerial serial;

    @PrePersist
    void prePersist() {
        if (orderDate == null) orderDate = LocalDateTime.now();
        if (currency == null)  currency = "VND";
        if (depositAmount == null) depositAmount = BigDecimal.ZERO;
        if (paymentStatus == null) paymentStatus = OrderPaymentStatus.UNPAID;
        if (status == null) status = OrderStatus.PROCESSING;

        updatedAt = LocalDateTime.now();

        BigDecimal price = (serial != null && serial.getVehicle() != null && serial.getVehicle().getPrice() != null)
                ? serial.getVehicle().getPrice()
                : BigDecimal.ZERO;
        remainingAmount = maxZero(price.subtract(depositAmount));
    }

    @PreUpdate
    void preUpdate() {
        BigDecimal price = (serial != null && serial.getVehicle() != null && serial.getVehicle().getPrice() != null)
                ? serial.getVehicle().getPrice()
                : BigDecimal.ZERO;

        if (depositAmount == null) depositAmount = BigDecimal.ZERO;
        remainingAmount = maxZero(price.subtract(depositAmount));
        updatedAt = LocalDateTime.now();
    }

    private static BigDecimal maxZero(BigDecimal v) {
        return v.signum() < 0 ? BigDecimal.ZERO : v;
    }
}