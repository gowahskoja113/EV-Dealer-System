package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.WarehouseRequest;
import com.swp391.evdealersystem.dto.response.WarehouseResponse;
import com.swp391.evdealersystem.dto.response.WarehouseStockResponse;
import com.swp391.evdealersystem.entity.Warehouse;
import com.swp391.evdealersystem.entity.WarehouseStock;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WarehouseMapper {

    public Warehouse toEntity(WarehouseRequest req) {
        Warehouse w = new Warehouse();
        w.setWarehouseName(req.getWarehouseName());
        w.setWarehouseLocation(req.getWarehouseLocation());
        w.setVehicleQuantity(0);
        return w;
    }

    public void updateEntity(Warehouse w, WarehouseRequest req) {
        w.setWarehouseName(req.getWarehouseName());
        w.setWarehouseLocation(req.getWarehouseLocation());
    }

    public WarehouseResponse toResponse(Warehouse w) {
        List<WarehouseStockResponse> items = w.getStocks().stream()
                .map(this::toItemResponse)
                .toList();

        int total = w.getStocks().stream()
                .mapToInt(WarehouseStock::getQuantity)
                .sum();

        WarehouseResponse res = new WarehouseResponse();
        res.setWarehouseId(w.getWarehouseId());
        res.setWarehouseName(w.getWarehouseName());
        res.setWarehouseLocation(w.getWarehouseLocation());
        res.setVehicleQuantity(total);
        res.setItems(items);
        return res;
    }

    private WarehouseStockResponse toItemResponse(WarehouseStock s) {
        var v = s.getVehicle();
        var m = v.getModel();

        WarehouseStockResponse r = new WarehouseStockResponse();
        r.setVehicleId(v.getVehicleId());
        if (m != null) {
            r.setModelCode(m.getModelCode());
            r.setBrand(m.getBrand());
            r.setColor(m.getColor());
            r.setProductionYear(m.getProductionYear());
        }
        r.setQuantity(s.getQuantity());
        return r;
    }
}
