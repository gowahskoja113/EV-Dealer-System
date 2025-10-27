package com.swp391.evdealersystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "electric_vehicle")
@Getter
@Setter
public class ElectricVehicle {

    @Id
    @Column(name = "vehicle_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicleId;

    @Column(nullable = false)
    private BigDecimal cost;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "battery_capacity", nullable = false)
    private Integer batteryCapacity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_id",
            referencedColumnName = "model_id",
            nullable = false)
    private Model model;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_vehicle_warehouse"))
    private Warehouse warehouse;

    @OneToMany(mappedBy = "vehicle", orphanRemoval = false)
    private List<Order> orders = new ArrayList<>();


}
