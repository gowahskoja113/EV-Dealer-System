package com.swp391.evdealersystem.entity;

import com.swp391.evdealersystem.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "appointments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_customer_slot", columnNames = {"customer_id","warehouse_id","start_at","end_at"})
        },
        indexes = {
                @Index(name="idx_capacity", columnList = "warehouse_id,service_id,start_at,end_at"),
                @Index(name="idx_customer_time", columnList = "customer_id,start_at")
        }
)
public class Appointment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    @Column(nullable=false) private Long customerId;
    @Column(nullable=false) private Long warehouseId;
    @Column(nullable=false) private Long serviceId;
    @Column(nullable=false) private Long assignedUserId;

    @Column(nullable=false) private LocalDateTime startAt;
    @Column(nullable=false) private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false) private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @Column(columnDefinition="TEXT") private String note;

    @Column(nullable=false, updatable=false) private LocalDateTime createdAt = LocalDateTime.now();
    @Column(nullable=false) private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate void touch() { this.updatedAt = LocalDateTime.now(); }


}
