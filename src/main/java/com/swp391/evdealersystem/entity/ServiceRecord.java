package com.swp391.evdealersystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name="service_record", indexes={
        @Index(columnList="createdAt"),
        @Index(columnList="user_id"),
        @Index(columnList="customer_id"),
        @Index(columnList="service_id"),
        @Index(name="ux_service_record_appointment", columnList="appointment_id", unique = true)
})
@EntityListeners(AuditingEntityListener.class)
@Data
public class ServiceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="user_id", nullable=false,
            foreignKey=@ForeignKey(name="fk_service_record_user"))
    private User user;

    @ManyToOne(optional=false)
    @JoinColumn(name="customer_id", nullable=false,
            foreignKey=@ForeignKey(name="fk_service_record_customer"))
    private Customer customer;

    @ManyToOne(optional=false)
    @JoinColumn(name="service_id", nullable=false,
            foreignKey=@ForeignKey(name="fk_service_record_service"))
    private ServiceEntity service;

    @CreatedDate
    @Column(nullable=false, updatable=false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Column(nullable=false, columnDefinition="text")
    private String content;

    @Column(columnDefinition="text")
    private String note;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_service_record_appointment"),
            unique = true)
    private Appointment appointment;

    @PrePersist
    public void prePersistSync() {

        // FIX lỗi createdAt = null
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = this.createdAt;
        }

        // Tự đồng bộ dữ liệu từ appointment
        if (appointment != null) {
            if (this.customer == null) this.customer = appointment.getCustomer();
            if (this.user == null) this.user = appointment.getAssignedUser();
            if (this.service == null) this.service = appointment.getService();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
