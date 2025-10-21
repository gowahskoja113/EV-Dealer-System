package com.swp391.evdealersystem.entity;

import com.swp391.evdealersystem.enums.CustomerStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(
        name = "customer",
        uniqueConstraints = @UniqueConstraint(name = "uq_customer_phone", columnNames = "phone_number"),
        indexes = @Index(name = "idx_customer_name", columnList = "name")
)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_customer_vehicle"))
    private ElectricVehicle vehicle;

    @OneToMany(mappedBy = "customer", orphanRemoval = false)
    private List<Order> orders = new ArrayList<>();

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String name;

    @NotBlank
    @Size(max = 32)
    @Column(name = "phone_number", nullable = false, length = 32, unique = true)
    private String phoneNumber;

    @Size(max = 255)
    @Column(name = "interest_vehicle", length = 255)
    private String interestVehicle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CustomerStatus status;
}
