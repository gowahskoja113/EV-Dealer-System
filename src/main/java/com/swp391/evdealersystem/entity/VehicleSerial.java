package com.swp391.evdealersystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vehicle_serial",
        indexes = {
                @Index(name = "idx_vs_model", columnList = "model_id"),
                @Index(name = "idx_vs_wh", columnList = "warehouse_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_vs_vin", columnNames = "vin")
        })
@Getter
@Setter
public class VehicleSerial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // “Xe đại diện” – lấy id dùng trong VIN
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private ElectricVehicle vehicle; // chính là bản ghi singleton của model

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private Model model;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "vin", length = 64, nullable = false, unique = true)
    private String vin;

    @Column(name = "color_code", length = 8, nullable = false)
    private String colorCode; // R, B, G, W,...

    @Column(name = "seq_no", nullable = false)
    private Integer seqNo;
}
