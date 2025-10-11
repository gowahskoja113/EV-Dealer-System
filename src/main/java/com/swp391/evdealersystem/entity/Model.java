package com.swp391.evdealersystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "model")
@Getter
@Setter
public class Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    private Long modelId;

    // 👇 Mã model (code hiển thị)
    @Column(name = "model_code", length = 50, unique = true, nullable = false)
    private String modelCode;

    @Column(length = 100, nullable = false)
    private String name;

    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ElectricVehicle> vehicles;
}
