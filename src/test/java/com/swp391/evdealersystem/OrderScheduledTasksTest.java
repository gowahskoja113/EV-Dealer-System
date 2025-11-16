package com.swp391.evdealersystem;

import com.swp391.evdealersystem.entity.Order;
import com.swp391.evdealersystem.enums.OrderStatus;
import com.swp391.evdealersystem.repository.CustomerRepository;
import com.swp391.evdealersystem.repository.OrderRepository;
import com.swp391.evdealersystem.repository.VehicleSerialRepository;
import com.swp391.evdealersystem.service.OrderScheduledTasks;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional // Đảm bảo test không lưu vĩnh viễn vào DB thật
class OrderScheduledTasksTest {

    @Autowired
    private OrderScheduledTasks orderScheduledTasks;

    @Autowired
    private OrderRepository orderRepository;

    // Giả sử bạn có các repo khác để tạo Order mẫu
    @Autowired private CustomerRepository customerRepository;
    @Autowired private VehicleSerialRepository serialRepository;


    @Test
    void testAutoCompleteDeliveringOrders() {
        // --- 1. CHUẨN BỊ DỮ LIỆU TEST ---
        // Giả sử bạn phải tạo Customer và Serial trước
        // (Bạn phải tự hoàn thiện phần này cho đúng với cấu trúc entity của bạn)
        // Customer customer = customerRepository.save(new Customer(..));
        // VehicleSerial serial = serialRepository.save(new VehicleSerial(..));

        Order order = new Order();
        order.setStatus(OrderStatus.DELIVERING); // Trạng thái cần test
        order.setDeliveryDate(LocalDate.now()); // Ngày giao là HÔM NAY
        // order.setCustomer(customer); // Set các field bắt buộc khác
        // order.setSerial(serial);     // Set các field bắt buộc khác

        Order savedOrder = orderRepository.save(order);
        Long orderId = savedOrder.getOrderId();

        // --- 2. GỌI HÀM CẦN TEST ---
        // Gọi trực tiếp, không cần chờ cron
        orderScheduledTasks.autoCompleteDeliveringOrders();

        // --- 3. KIỂM TRA KẾT QUẢ ---
        Order updatedOrder = orderRepository.findById(orderId).orElseThrow();

        // Kiểm tra xem status đã đổi thành COMPLETED chưa
        assertEquals(OrderStatus.COMPLETED, updatedOrder.getStatus());
    }
}