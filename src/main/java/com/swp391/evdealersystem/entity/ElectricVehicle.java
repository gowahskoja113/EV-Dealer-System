package com.swp391.evdealersystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Table(name = "electric_vehicle")
@Getter @Setter
public class ElectricVehicle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(precision = 15, scale = 2, nullable = false)
    private long cost;

    @Column(precision = 15, scale = 2, nullable = false)
    private long price;

    @Column(name = "battery_capacity", nullable = false)
    private Integer batteryCapacity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_id", nullable = false)
    private Model model;
}
