package com.swp391.evdealersystem.dto.response;

import com.swp391.evdealersystem.entity.Order;
import com.swp391.evdealersystem.enums.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long customerId;
    private String customerName;
    private Long vehicleId;
    private String vehicleModel;

    private BigDecimal totalAmount;

    private BigDecimal depositAmount;
    private OrderStatus status;
    private OrderPaymentStatus paymentStatus;
    private LocalDate deliveryDate;
    private LocalDateTime orderDate;

    // Giữ nguyên constructor RẤT TỐT này của bạn
    public OrderResponse(Order order) {
        this.orderId = order.getOrderId();

        // Cẩn thận: Lấy totalAmount từ serial.vehicle.price thì CHÍNH XÁC hơn
        if (order.getSerial() != null && order.getSerial().getVehicle() != null) {
            this.totalAmount = order.getSerial().getVehicle().getPrice();
            this.vehicleId = order.getSerial().getVehicle().getVehicleId();

            if (order.getSerial().getVehicle().getModel() != null) {
                // Tốt! logic mới của bạn lấy getBrand()
                this.vehicleModel = order.getSerial().getVehicle().getModel().getBrand();
            }
        } else {
            // Fallback nếu vehicle bị null (ít khi xảy ra)
            this.totalAmount = order.getRemainingAmount().add(order.getDepositAmount());
        }

        this.depositAmount = order.getDepositAmount();
        this.status = order.getStatus();
        this.paymentStatus = order.getPaymentStatus();
        this.deliveryDate = order.getDeliveryDate();
        this.orderDate = order.getOrderDate();

        if (order.getCustomer() != null) {
            this.customerId = order.getCustomer().getCustomerId();
            this.customerName = order.getCustomer().getName();
        }
    }
}
