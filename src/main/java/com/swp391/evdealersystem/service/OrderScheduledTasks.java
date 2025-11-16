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

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void autoCompleteDeliveringOrders() {
        LocalDate today = LocalDate.now();

        List<Order> ordersToComplete = orderRepo.findAllByStatusAndDeliveryDateLessThanEqual(
                OrderStatus.DELIVERING,
                today
        );

        if (!ordersToComplete.isEmpty()) {
            for (Order order : ordersToComplete) {
                order.setStatus(OrderStatus.COMPLETED);
            }
            orderRepo.saveAll(ordersToComplete);
        }
    }
}