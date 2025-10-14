package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.ElectricVehicleRequest;
import com.swp391.evdealersystem.dto.response.ElectricVehicleResponse;
import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.Model;
import org.springframework.stereotype.Component;
@Component
public class ElectricVehicleMapper {
    public ElectricVehicle toEntity(ElectricVehicleRequest req, Model model) {
        if (req == null) return null;
        ElectricVehicle ev = new ElectricVehicle();
        ev.setCost(req.getCost());
        ev.setPrice(req.getPrice());
        ev.setBatteryCapacity(req.getBatteryCapacity());
        ev.setModel(model);
        return ev;
    }

    // update KHÔNG đụng tới model
    public void updateEntity(ElectricVehicle ev, ElectricVehicleRequest req) {
        if (ev == null || req == null) return;
        if (req.getCost() != null) ev.setCost(req.getCost());
        if (req.getPrice() != null) ev.setPrice(req.getPrice());
        if (req.getBatteryCapacity() != null) ev.setBatteryCapacity(req.getBatteryCapacity());
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
        }
        return r;
    }
}
