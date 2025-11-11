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
        @Index(name="ux_service_record_appointment", columnList="appointment_id", unique = true) // đảm bảo 1-1
})
@EntityListeners(AuditingEntityListener.class)
@Data
public class ServiceRecord {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;

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

    @CreatedDate @Column(nullable=false, updatable=false) private Instant createdAt;
    @LastModifiedDate private Instant updatedAt;

    @Column(nullable=false, columnDefinition="text") private String content;
    @Column(columnDefinition="text") private String note;

    // BÊN SỞ HỮU quan hệ 1-1
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_service_record_appointment"),
            unique = true) // unique ở cột này
    private Appointment appointment;

    // (tuỳ chọn) tự đồng bộ nếu dev quên set tay:
    @PrePersist
    public void prePersistSync() {
        if (appointment != null) {
            if (this.customer == null) this.customer = appointment.getCustomer();
            if (this.user == null) this.user = appointment.getAssignedUser();
            if (this.service == null) this.service = appointment.getService();
        }
    }
}