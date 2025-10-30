    package com.swp391.evdealersystem.entity;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.Setter;

    import java.util.ArrayList;
    import java.util.List;

    @Entity
    @Table(name = "warehouse")
    @Getter
    @Setter
    public class Warehouse {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "warehouse_id")
        private Long warehouseId;

        @Column(name = "warehouse_location", nullable = false, length = 255)
        private String warehouseLocation;

        @Column(name = "warehouse_name", nullable = false, length = 255)
        private String warehouseName;

        @Column(name = "vehicle_quantity", nullable = false)
        private Integer vehicleQuantity = 0;

        @JsonIgnore
        @OneToMany(mappedBy = "warehouse", cascade = CascadeType.PERSIST, orphanRemoval = true)
        private List<ElectricVehicle> vehicles = new ArrayList<>();

        @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<WarehouseStock> stocks = new ArrayList<>();
    }
