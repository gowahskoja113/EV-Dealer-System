package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.ElectricVehicleRequest;
import com.swp391.evdealersystem.dto.response.ElectricVehicleResponse;
import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.Model;
import com.swp391.evdealersystem.entity.Warehouse;
import com.swp391.evdealersystem.enums.VehicleStatus;
import org.springframework.stereotype.Component;

@Component
public class ElectricVehicleMapper {

    public ElectricVehicle toEntity(ElectricVehicleRequest req, Model model, Warehouse warehouse) {
        if (req == null) return null;
        ElectricVehicle ev = new ElectricVehicle();
        ev.setCost(req.getCost());
        ev.setPrice(req.getPrice());
        ev.setBatteryCapacity(req.getBatteryCapacity());
        ev.setModel(model);
        ev.setWarehouse(warehouse);
        ev.setImageUrl(req.getImageUrl());
        ev.setStatus(req.getStatus() != null ? req.getStatus() : VehicleStatus.AVAILABLE);
        // holdUntil thường được service quản lý theo luồng hold, không set từ request create
        return ev;
    }

    /**
     * Update các field cơ bản.
     * Nếu muốn đổi model/warehouse, truyền model/warehouse khác (có thể null nếu không đổi).
     */
    public void updateEntity(ElectricVehicle ev, ElectricVehicleRequest req, Model model, Warehouse warehouse) {
        if (ev == null || req == null) return;
        if (req.getCost() != null) ev.setCost(req.getCost());
        if (req.getPrice() != null) ev.setPrice(req.getPrice());
        if (req.getBatteryCapacity() != null) ev.setBatteryCapacity(req.getBatteryCapacity());
        if (req.getImageUrl() != null) ev.setImageUrl(req.getImageUrl());
        if (req.getStatus() != null) ev.setStatus(req.getStatus());
        if (model != null) ev.setModel(model);
        if (warehouse != null) ev.setWarehouse(warehouse);
        // holdUntil không nên cập nhật trực tiếp từ request thường, hãy dùng service hold
    }

    public ElectricVehicleResponse toResponse(ElectricVehicle ev) {
        if (ev == null) return null;
        ElectricVehicleResponse r = new ElectricVehicleResponse();
        r.setVehicleId(ev.getVehicleId());
        r.setCost(ev.getCost());
        r.setPrice(ev.getPrice());
        r.setBatteryCapacity(ev.getBatteryCapacity());

        if (ev.getModel() != null) {
            r.setModelId(ev.getModel().getModelId());
            r.setModelCode(ev.getModel().getModelCode());
            r.setBrand(ev.getModel().getBrand());
            r.setColor(ev.getModel().getColor());
            r.setProductionYear(ev.getModel().getProductionYear());
        }

        if (ev.getWarehouse() != null) {
            r.setWarehouseId(ev.getWarehouse().getWarehouseId());
        }

        r.setImageUrl(ev.getImageUrl());
        r.setStatus(ev.getStatus());
        r.setHoldUntil(ev.getHoldUntil());
        r.setSelectableNow(ev.isSelectableNow());
        return r;
    }
}
