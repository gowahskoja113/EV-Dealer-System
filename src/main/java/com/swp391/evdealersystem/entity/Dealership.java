package com.swp391.evdealersystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dealership")
@Getter
@Setter
public class Dealership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dealership_id")
    private Long dealershipId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @OneToMany(
            mappedBy = "dealership",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnore // Tránh lặp vô hạn khi serialize JSON
    private List<Warehouse> warehouses = new ArrayList<>();
}