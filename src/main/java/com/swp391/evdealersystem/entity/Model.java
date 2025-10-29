package com.swp391.evdealersystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "model")
@Getter @Setter
@ToString(exclude = "vehicles")
@EqualsAndHashCode(of = "modelId")
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    private Long modelId;

    @Column(name = "model_code", length = 50, unique = true, nullable = false)
    private String modelCode;

    @Column(length = 100, nullable = true)
    private String brand;

    @Column(name = "model_color", length = 100, nullable = true)
    private String color;

    private Integer productionYear;

    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ElectricVehicle> vehicles;

    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WarehouseStock> warehouseStocks = new ArrayList<>();
}