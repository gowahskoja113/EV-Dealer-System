package com.swp391.evdealersystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "warehouse_stock",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_stock_warehouse_model",
                columnNames = {"warehouse_id", "model_id"}
        ),
        indexes = {
                @Index(name = "idx_stock_wh", columnList = "warehouse_id"),
                @Index(name = "idx_stock_model", columnList = "model_id")
        }
)
@Getter
@Setter
public class WarehouseStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long stockId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_stock_warehouse"))
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_stock_model"))
    private Model model;

    @Column(nullable = false)
    private Integer quantity;
}
