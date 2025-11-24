package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.entity.Order;
import com.swp391.evdealersystem.enums.OrderStatus;
import com.swp391.evdealersystem.repository.OrderRepository;
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

//"0 0 1 * * ?" (1h sáng).
    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void autoCompleteDeliveringOrders() {
        LocalDate today = LocalDate.now();
        System.out.println("---- [SCHEDULER] Đang quét đơn hàng đến hạn giao ngày: " + today + " ----");

        List<Order> ordersToComplete = orderRepo.findAllByStatusAndDeliveryDateLessThanEqual(
                OrderStatus.DELIVERING,
                today
        );

        if (!ordersToComplete.isEmpty()) {
            for (Order order : ordersToComplete) {
                order.setStatus(OrderStatus.COMPLETED);
                System.out.println("-> Auto Complete Order ID: " + order.getOrderId());
            }

            orderRepo.saveAll(ordersToComplete);

            System.out.println("-> Đã lưu thành công " + ordersToComplete.size() + " đơn hàng.");
        } else {
            System.out.println("-> Không có đơn hàng nào cần xử lý.");
        }
    }
}