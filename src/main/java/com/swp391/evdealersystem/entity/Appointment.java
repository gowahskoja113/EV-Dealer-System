package com.swp391.evdealersystem.entity;

import com.swp391.evdealersystem.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "appointments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_customer_slot",
                        columnNames = {"customer_id", "service_id", "start_at", "end_at"})
        })
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_appointment_customer"))
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assigned_user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_appointment_assigned_user"))
    private User assignedUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_appointment_service"))
    private ServiceEntity service;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)  // Liên kết với Slot
    @JoinColumn(name = "slot_id", nullable = false, foreignKey = @ForeignKey(name = "fk_appointment_slot"))
    private Slot slot;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @Column(columnDefinition = "TEXT")
    private String note;

    @PrePersist
    void prePersist() {
        // Gán ngày tạo mặc định
        if (this.startAt == null) this.startAt = LocalDateTime.now();
        if (this.endAt == null) this.endAt = startAt.plusHours(1); // mặc định mỗi cuộc hẹn 1 giờ
    }

}
