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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(length = 100)
    private String brand;

    @Column(precision = 15, scale = 2)
    private long cost;

    @Column(precision = 15, scale = 2)
    private long price;

    @Column(name = "battery_capacity")
    private int batteryCapacity;

    // Khóa ngoại trỏ đến model
    @ManyToOne
    @JoinColumn(name = "model_id", nullable = false)
    private Model model;
}
