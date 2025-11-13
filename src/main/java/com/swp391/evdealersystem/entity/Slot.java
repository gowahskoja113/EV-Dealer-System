package com.swp391.evdealersystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "slots")
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long slotId;

    @Column(nullable = false)
    private LocalDateTime startTime; // Thời gian bắt đầu slot

    @Column(nullable = false)
    private LocalDateTime endTime; // Thời gian kết thúc slot

    @Column(nullable = false)
    private Integer maxTestDrive; // Giới hạn số lượng lái thử

    @Column(nullable = false)
    private Integer maxService; // Giới hạn số lượng bảo dưỡng

    @Column(nullable = false)
    private Integer testDriveCount = 0; // Số người đã đăng ký lái thử

    @Column(nullable = false)
    private Integer serviceCount = 0; // Số người đã đăng ký bảo dưỡng

    // Kiểm tra nếu slot còn chỗ cho lái thử
    public boolean isAvailableForTestDrive() {
        return testDriveCount < maxTestDrive;
    }

    // Kiểm tra nếu slot còn chỗ cho bảo dưỡng
    public boolean isAvailableForService() {
        return serviceCount < maxService;
    }

    // Tăng số lượng đăng ký cho dịch vụ
    public void incrementTestDriveCount() {
        if (isAvailableForTestDrive()) {
            testDriveCount++;
        }
    }

    public void incrementServiceCount() {
        if (isAvailableForService()) {
            serviceCount++;
        }
    }

    // Giảm số lượng đăng ký khi hủy cuộc hẹn
    public void decrementTestDriveCount() {
        if (testDriveCount > 0) {
            testDriveCount--;
        }
    }

    public void decrementServiceCount() {
        if (serviceCount > 0) {
            serviceCount--;
        }
    }


}
