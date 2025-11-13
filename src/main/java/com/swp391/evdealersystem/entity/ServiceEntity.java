package com.swp391.evdealersystem.entity;

import com.swp391.evdealersystem.enums.ServiceType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "service_entity", indexes = @Index(columnList = "name", unique = true))
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;  // Tên dịch vụ (Lái thử, Bảo dưỡng, v.v.)

    @Column(columnDefinition = "text")
    private String description;  // Mô tả dịch vụ

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
