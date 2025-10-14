package com.swp391.evdealersystem.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

    @Column(length = 100, nullable = false)
    private String brand;

    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ElectricVehicle> vehicles;
}