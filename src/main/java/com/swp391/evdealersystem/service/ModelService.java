package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.ModelRequest;
import com.swp391.evdealersystem.dto.response.ModelResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ModelService {
    ModelResponse create(ModelRequest req);
    ModelResponse getById(Long id);
    ModelResponse getByCode(String modelCode);
    List<ModelResponse> list(Pageable pageable);
    ModelResponse update(Long id, ModelRequest req);
    void delete(Long id);
}
