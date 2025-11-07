package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.WarehouseRequest;
import com.swp391.evdealersystem.dto.response.VehicleBrief;
import com.swp391.evdealersystem.dto.response.WarehouseResponse;
import com.swp391.evdealersystem.dto.response.WarehouseStockResponse;
import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.Warehouse;
import com.swp391.evdealersystem.entity.WarehouseStock;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WarehouseMapper {

    public Warehouse toEntity(WarehouseRequest req) {
        Warehouse w = new Warehouse();
        w.setWarehouseLocation(req.getWarehouseLocation());
        w.setWarehouseName(req.getWarehouseName());
        w.setVehicleQuantity(0);
        if (req.getMaxCapacity() != null) {
            w.setMaxCapacity(req.getMaxCapacity());
        }
        return w;
    }

    public void updateEntity(Warehouse w, WarehouseRequest req) {
        w.setWarehouseName(req.getWarehouseName());
        w.setWarehouseLocation(req.getWarehouseLocation());
        if (req.getMaxCapacity() != null) {
            w.setMaxCapacity(req.getMaxCapacity());
        }
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
        res.setMaxCapacity(w.getMaxCapacity());
        res.setDealershipId(w.getWarehouseId());
        res.setItems(items);
        return res;
    }

    public VehicleBrief toVehicleBrief(ElectricVehicle v) {
        VehicleBrief b = new VehicleBrief();
        b.setVehicleId(v.getVehicleId());
        b.setImageUrl(v.getImageUrl());
        b.setStatus(v.getStatus());
        b.setSelectableNow(v.isSelectableNow());
        return b;
    }

    public WarehouseStockResponse toItemResponse(WarehouseStock s) {
        var m = s.getModel();
        WarehouseStockResponse r = new WarehouseStockResponse();
        r.setModelCode(m.getModelCode());
        r.setBrand(m.getBrand());
        r.setColor(m.getColor());
        r.setProductionYear(m.getProductionYear());
        r.setQuantity(s.getQuantity());
        return r;
    }
}
