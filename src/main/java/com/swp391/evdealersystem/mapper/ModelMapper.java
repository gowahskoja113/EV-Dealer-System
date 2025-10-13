package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.ModelRequest;
import com.swp391.evdealersystem.dto.response.ModelResponse;
import com.swp391.evdealersystem.entity.Model;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {

    public Model toEntity(ModelRequest req) {
        if (req == null) return null;
        Model m = new Model();
        m.setModelCode(req.getModelCode());
        m.setBrand(req.getBrand());
        return m;
    }

    public void updateEntity(Model m, ModelRequest req) {
        if (req == null || m == null) return;
        if (req.getModelCode() != null) m.setModelCode(req.getModelCode());
        if (req.getBrand() != null) m.setBrand(req.getBrand());
    }

    public ModelResponse toResponse(Model m) {
        if (m == null) return null;
        ModelResponse r = new ModelResponse();
        r.setModelId(m.getModelId());
        r.setModelCode(m.getModelCode());
        r.setBrand(m.getBrand());
        return r;
    }
}
