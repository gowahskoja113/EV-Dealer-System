package com.swp391.evdealersystem.entity;

import com.swp391.evdealersystem.enums.VehicleStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "electric_vehicle",
        indexes = {
                @Index(name = "idx_ev_status", columnList = "status"),
                @Index(name = "idx_ev_warehouse_status", columnList = "warehouse_id,status"),
                @Index(name = "idx_ev_hold_until", columnList = "hold_until")
        })
@Getter
@Setter
public class ElectricVehicle {

    @Id
    @Column(name = "vehicle_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicleId;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal cost;

    @Column(nullable = false, precision = 18, scale = 2)
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

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    @Column(name = "hold_until")
    private OffsetDateTime holdUntil;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WarehouseStock> warehouseStocks = new ArrayList<>();

    // helper: check xe co duoc tao order hay khong
    @Transient
    public boolean isSelectableNow() {
        if (status == VehicleStatus.SOLD_OUT) return false;
        if (status == VehicleStatus.HOLD && holdUntil != null) {
            return OffsetDateTime.now().isAfter(holdUntil);
        }
        return status == VehicleStatus.AVAILABLE;
    }
}
