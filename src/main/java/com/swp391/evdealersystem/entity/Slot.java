package com.swp391.evdealersystem.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Setter
@Getter
@Entity
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startTime;  // Thời gian bắt đầu của slot

    @Column(nullable = false)
    private LocalDateTime endTime;    // Thời gian kết thúc của slot

    @Column(nullable = false)
    private int maxTestDrive = 5;     // Số lượng dịch vụ lái thử tối đa

    @Column(nullable = false)
    private int maxService = 10;      // Số lượng dịch vụ bảo dưỡng tối đa

    @Column(nullable = false)
    private int testDriveCount = 0;   // Số lượng dịch vụ lái thử đã đặt

    @Column(nullable = false)
    private int serviceCount = 0;     // Số lượng dịch vụ bảo dưỡng đã đặt

    @OneToMany(mappedBy = "slot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments;  // Liên kết với các lịch hẹn (appointments)

    // Method to check if the slot is full for a certain service type
    public boolean isFullForTestDrive() {
        return testDriveCount >= maxTestDrive;
    }

    public boolean isFullForService() {
        return serviceCount >= maxService;
    }

    // Method to check if the slot is available for a given time
    public boolean isAvailable(LocalDateTime appointmentTime) {
        return !appointmentTime.isBefore(startTime) && !appointmentTime.isAfter(endTime);
    }
}
