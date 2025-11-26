package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.entity.Order;
import com.swp391.evdealersystem.entity.VehicleSerial;
import com.swp391.evdealersystem.enums.OrderStatus;
import com.swp391.evdealersystem.enums.VehicleStatus;
import com.swp391.evdealersystem.repository.OrderRepository;
import com.swp391.evdealersystem.repository.VehicleSerialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Thêm cái này để log cho chuyên nghiệp
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderScheduledTasks {

    private final OrderRepository orderRepo;
    private final VehicleSerialRepository vehicleSerialRepo;

    // Chạy định kỳ (Ví dụ: 1h sáng mỗi ngày, hoặc mỗi 30p tùy cấu hình)
    // cron = "0 0 1 * * ?" -> 1h sáng
    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void autoCompleteDeliveringOrders() {
        LocalDate today = LocalDate.now();
        log.info("---- [SCHEDULER] Bắt đầu quét đơn hàng ĐANG GIAO đến hạn: {} ----", today);

        // 1. Tìm các đơn đang DELIVERING mà ngày giao <= hôm nay
        List<Order> ordersToComplete = orderRepo.findAllByStatusAndDeliveryDateLessThanEqual(
                OrderStatus.DELIVERING,
                today
        );

        if (ordersToComplete.isEmpty()) {
            log.info("-> Không có đơn hàng nào cần hoàn tất.");
            return;
        }

        for (Order order : ordersToComplete) {
            // A. Xử lý Xe trước (Xe phải giao xong thì đơn mới xong)
            VehicleSerial serial = order.getSerial();
            if (serial != null) {
                // Chỉ update nếu xe chưa phải là DELIVERED
                if (serial.getStatus() != VehicleStatus.DELIVERED) {
                    serial.setStatus(VehicleStatus.DELIVERED);
                    serial.setHoldUntil(null); // Xóa thời gian giữ xe

                    vehicleSerialRepo.save(serial);
                    log.info("   -> [AUTO] Vehicle VIN {} chuyển sang DELIVERED", serial.getVin());
                }
            }

            // B. Xử lý Đơn hàng
            order.setStatus(OrderStatus.COMPLETED);
            // Lưu ý: Không set lại fullyPaidAt vì nó đã được set lúc thanh toán rồi
            // Chỉ cập nhật thời gian sửa đổi cuối cùng
            order.setUpdatedAt(LocalDateTime.now());

            log.info("-> [AUTO] Order ID {} chuyển sang COMPLETED", order.getOrderId());
        }

        // C. Lưu tất cả đơn hàng
        orderRepo.saveAll(ordersToComplete);
        log.info("-> Đã hoàn tất tự động {} đơn hàng.", ordersToComplete.size());
    }
}