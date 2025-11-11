package com.swp391.evdealersystem.entity;

import com.swp391.evdealersystem.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "appointments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_customer_slot",
                        columnNames = {"customer_id","warehouse_id","start_at","end_at"})
        },
        indexes = {
                @Index(name="idx_capacity", columnList = "warehouse_id,service_id,start_at,end_at"),
                @Index(name="idx_customer_time", columnList = "customer_id,start_at"),
                @Index(name="idx_assigned_user_time", columnList = "assigned_user_id,start_at")
        }
)
public class Appointment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    // Nhiều cuộc hẹn thuộc 1 khách hàng
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_appointment_customer"))
    @ToString.Exclude
    private Customer customer;

    // Nhiều cuộc hẹn do 1 nhân viên phụ trách
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assigned_user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_appointment_assigned_user"))
    @ToString.Exclude
    private User assignedUser;

    // Getter và Setter cho ServiceEntity
    // Dịch vụ của lịch hẹn
    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_appointment_service"))
    @ToString.Exclude
    private ServiceEntity service;

    // Nếu có entity Warehouse thì nên ManyToOne; tạm thời để Long
    @Column(name = "warehouse_id", nullable=false)
    private Long warehouseId;

    @Column(name = "start_at", nullable=false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable=false)
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @Column(columnDefinition="TEXT")
    private String note;

    @Column(nullable=false, updatable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable=false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate void touch() { this.updatedAt = LocalDateTime.now(); }

    // ---- 1–1 với ServiceRecord (ServiceRecord là bên sở hữu @JoinColumn)
    @OneToOne(mappedBy = "appointment",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @ToString.Exclude
    private ServiceRecord serviceRecord;


//    // Helper (không bắt buộc)
//    public void setServiceRecord(ServiceRecord sr) {
//        if (sr == null) {
//            if (this.serviceRecord != null) this.serviceRecord.setAppointment(null);
//            this.serviceRecord = null;
//        } else {
//            this.serviceRecord = sr;
//            sr.setAppointment(this);
//            // đồng bộ ngữ nghĩa (tuỳ chọn)
//            if (sr.getCustomer() == null) sr.setCustomer(this.getCustomer());
//            if (sr.getUser() == null) sr.setUser(this.getAssignedUser());
//            if (sr.getService() == null) sr.setService(this.getService());
//        }
//
}
