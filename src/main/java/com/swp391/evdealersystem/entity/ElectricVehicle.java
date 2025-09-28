package com.swp391.evdealersystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "electric_vehicle")
@Getter
@Setter
public class ElectricVehicle {

    @Id
    @Column(name = "vehicle_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicleId;

    @Column(length = 100, nullable = false)
    private String model;

    @Column(precision = 15, scale = 2)
    private long cost;

    @Column(length = 100)
    private String brand;

    @Column(precision = 15, scale = 2)
    private long price;

    @Column(name = "battery_capacity")
    private int batteryCapacity;
}