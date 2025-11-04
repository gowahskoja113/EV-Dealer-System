package com.swp391.evdealersystem.entity;

import com.swp391.evdealersystem.enums.VehicleStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "vehicle_serial",
        indexes = {
                @Index(name = "idx_vs_wh", columnList = "warehouse_id"),
                @Index(name = "idx_vs_status", columnList = "status"),
                @Index(name = "idx_vs_hold_until", columnList = "hold_until")
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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private ElectricVehicle vehicle;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private Model model;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "vin", length = 64, nullable = false, unique = true)
    private String vin;

    @Column(name = "color_code", length = 8, nullable = false)
    private String colorCode;

    @Column(name = "seq_no", nullable = false)
    private Integer seqNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = true, length = 20)
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    @Column(name = "hold_until")
    private OffsetDateTime holdUntil;

    @Transient
    public boolean isSelectableNow() {
        if (status == VehicleStatus.SOLD_OUT) return false;
        if (status == VehicleStatus.HOLD && holdUntil != null) {
            // Có thể chọn nếu đã hết hạn hold
            return OffsetDateTime.now().isAfter(holdUntil);
        }
        // Có thể chọn nếu trạng thái là AVAILABLE
        return status == VehicleStatus.AVAILABLE;
    }
}