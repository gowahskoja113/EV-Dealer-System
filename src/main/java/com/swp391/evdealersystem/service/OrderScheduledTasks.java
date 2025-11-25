package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.entity.Order;
import com.swp391.evdealersystem.entity.VehicleSerial;
import com.swp391.evdealersystem.enums.OrderStatus;
import com.swp391.evdealersystem.enums.VehicleStatus;
import com.swp391.evdealersystem.repository.OrderRepository;
import com.swp391.evdealersystem.repository.VehicleSerialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderScheduledTasks {

    private final OrderRepository orderRepo;
    private final VehicleSerialRepository vehicleSerialRepo;

    // "0 0 1 * * ?" (1h sáng). Hiện tại cron này đang chạy mỗi phút giây thứ 0
    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void autoCompleteDeliveringOrders() {
        LocalDate today = LocalDate.now();
        System.out.println("---- [SCHEDULER] dang quet don hang den han ngay giao: " + today + " ----");

        // Tìm các đơn đang giao (DELIVERING) mà ngày giao <= hôm nay
        List<Order> ordersToComplete = orderRepo.findAllByStatusAndDeliveryDateLessThanEqual(
                OrderStatus.DELIVERING,
                today
        );

        if (!ordersToComplete.isEmpty()) {
            for (Order order : ordersToComplete) {
                // 1. Update trạng thái đơn hàng -> COMPLETED
                order.setStatus(OrderStatus.COMPLETED);

                // 2. Update trạng thái xe -> DELIVERED
                VehicleSerial serial = order.getSerial();
                if (serial != null) {
                    if (serial.getStatus() != VehicleStatus.DELIVERED) {
                        serial.setStatus(VehicleStatus.DELIVERED);
                        serial.setHoldUntil(null); // Clear hold cho chắc chắn

                        vehicleSerialRepo.save(serial); // Lưu xe
                        System.out.println("   -> Auto Update Vehicle VIN: " + serial.getVin() + " to DELIVERED");
                    }
                }

                System.out.println("-> Auto Complete Order ID: " + order.getOrderId());
            }

            orderRepo.saveAll(ordersToComplete);

            System.out.println("-> Da luu thanh cong " + ordersToComplete.size() + " don hang va cap nhat trang thai xe.");
        } else {
            System.out.println("-> khong co don hang nao can hoan thanh.");
        }
    }
}