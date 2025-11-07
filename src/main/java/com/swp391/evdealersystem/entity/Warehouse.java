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

    @Column(name = "vehicle_quantity", nullable = true)
    private Integer vehicleQuantity = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealership_id", nullable = false) // Cột khóa ngoại trong bảng 'warehouse'
    @JsonIgnore
    private Dealership dealership;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    private List<WarehouseStock> stocks = new ArrayList<>();
}