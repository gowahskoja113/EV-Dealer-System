package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.ModelRequest;
import com.swp391.evdealersystem.dto.response.ModelResponse;
import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.Model;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ModelMapper {

    // Request -> Entity (create)
    public Model toEntity(ModelRequest req) {
        if (req == null) return null;
        Model m = new Model();
        m.setModelCode(req.getModelCode());
        m.setName(req.getName());
        return m;
    }

    // Update entity từ request (bỏ qua null/blank)
    public void updateEntity(Model entity, ModelRequest req) {
        if (entity == null || req == null) return;

        if (req.getModelCode() != null) {
            String code = req.getModelCode().trim();
            if (!code.isEmpty() && !Objects.equals(code, entity.getModelCode())) {
                entity.setModelCode(code);
            }
        }
        if (req.getName() != null) {
            String name = req.getName().trim();
            if (!name.isEmpty() && !Objects.equals(name, entity.getName())) {
                entity.setName(name);
            }
        }
    }

    // Entity -> Response (kèm tóm tắt danh sách xe)
    public ModelResponse toResponse(Model entity) {
        if (entity == null) return null;
        ModelResponse res = new ModelResponse();
        res.setModelId(entity.getModelId());
        res.setModelCode(entity.getModelCode());
        res.setName(entity.getName());
        res.setVehicles(toVehicleSummaries(entity.getVehicles()));
        return res;
    }

    // List<ElectricVehicle> -> List<Summary>
    public List<ModelResponse.ElectricVehicleSummary> toVehicleSummaries(List<ElectricVehicle> vehicles) {
        List<ModelResponse.ElectricVehicleSummary> out = new ArrayList<>();
        if (vehicles == null) return out;
        for (ElectricVehicle v : vehicles) {
            out.add(toVehicleSummary(v));
        }
        return out;
    }

    public ModelResponse.ElectricVehicleSummary toVehicleSummary(ElectricVehicle v) {
        if (v == null) return null;
        ModelResponse.ElectricVehicleSummary s = new ModelResponse.ElectricVehicleSummary();
        s.setVehicleId(v.getVehicleId());
        s.setBrand(v.getBrand());
        s.setBatteryCapacity(v.getBatteryCapacity());
        s.setPrice(v.getPrice());
        return s;
    }
}
