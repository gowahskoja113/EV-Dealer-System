package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.WarehouseRequest;
import com.swp391.evdealersystem.dto.response.WarehouseStockResponse;
import com.swp391.evdealersystem.dto.response.WarehouseResponse;
import com.swp391.evdealersystem.entity.Warehouse;
import com.swp391.evdealersystem.entity.WarehouseStock;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WarehouseMapper {

    public Warehouse toEntity(WarehouseRequest req) {
        Warehouse w = new Warehouse();
        w.setWarehouseLocation(req.getWarehouseLocation());
        w.setVehicleQuantity(0);
        w.setWarehouseName(req.getWarehouseName());
        return w;
    }

    public void updateEntity(Warehouse w, WarehouseRequest req) {
        w.setWarehouseLocation(req.getWarehouseLocation());
        w.setWarehouseName(req.getWarehouseName());
    }

    public WarehouseResponse toResponse(Warehouse w) {
        List<WarehouseStockResponse> items = w.getStocks().stream()
                .map(this::toItemResponse)
                .toList();

        int total = w.getStocks().stream()
                .map(WarehouseStock::getQuantity)
                .reduce(0, Integer::sum);

        return WarehouseResponse.builder()
                .warehouseId(w.getWarehouseId())
                .warehouseLocation(w.getWarehouseLocation())
                .warehouseName(w.getWarehouseName())
                .vehicleQuantity(total)
                .items(items)
                .build();
    }

    private WarehouseStockResponse toItemResponse(WarehouseStock s) {
        var v = s.getVehicle();
        var m = v.getModel();

        return WarehouseStockResponse.builder()
                .vehicleId(v.getVehicleId())
                .modelCode(m != null ? m.getModelCode() : null)
                .brand(m != null ? m.getBrand() : null)
                .color(m != null ? m.getColor() : null)
                .productionYear(m != null ? m.getProductionYear() : null)
                .quantity(s.getQuantity())
                .build();
    }
}
