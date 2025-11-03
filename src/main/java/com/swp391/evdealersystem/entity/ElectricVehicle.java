package com.swp391.evdealersystem.entity;

import com.swp391.evdealersystem.enums.VehicleStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "electric_vehicle",
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_ev_model", columnNames = "model_id")
        },
        indexes = {
                @Index(name = "idx_ev_status", columnList = "status")
        })
@Getter
@Setter
public class ElectricVehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "vin", length = 64, unique = true)
    private String vin;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal cost;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Column(name = "battery_capacity", nullable = false)
    private Integer batteryCapacity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_id", nullable = false)
    private Model model;

    @OneToMany(mappedBy = "vehicle", orphanRemoval = false)
    private List<Order> orders = new ArrayList<>();

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    @Transient
    public boolean isSelectableNow() {
        if (status == VehicleStatus.SOLD_OUT) {
            return false;
        } else {
            return status == VehicleStatus.AVAILABLE;
        }
    }
}
