package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.ModelRequest;
import com.swp391.evdealersystem.dto.response.ModelResponse;
import com.swp391.evdealersystem.entity.Model;
import com.swp391.evdealersystem.mapper.ModelMapper;
import com.swp391.evdealersystem.repository.ModelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ModelServiceImpl implements ModelService {

    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;

    @Override
    public ModelResponse create(ModelRequest req) {
        if (req == null || req.getModelCode() == null || req.getModelCode().isBlank()) {
            throw new IllegalArgumentException("modelCode không được trống");
        }
        if (modelRepository.existsByModelCode(req.getModelCode().trim())) {
            throw new DataIntegrityViolationException("modelCode đã tồn tại");
        }
        Model entity = modelMapper.toEntity(req);
        entity = modelRepository.save(entity);
        return modelMapper.toResponse(entity);
    }

    @Override
    public ModelResponse getById(Long id) {
        Model entity = modelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy model id=" + id));
        return modelMapper.toResponse(entity);
    }

    @Override
    public ModelResponse getByCode(String modelCode) {
        Model entity = modelRepository.findByModelCode(modelCode)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy modelCode=" + modelCode));
        return modelMapper.toResponse(entity);
    }

    @Override
    public Page<ModelResponse> list(Pageable pageable) {
        return modelRepository.findAll(pageable).map(modelMapper::toResponse);
    }

    @Override
    public ModelResponse update(Long id, ModelRequest req) {
        Model entity = modelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy model id=" + id));

        // kiểm tra unique nếu đổi modelCode
        if (req != null && req.getModelCode() != null) {
            String newCode = req.getModelCode().trim();
            if (!newCode.isEmpty() && !newCode.equals(entity.getModelCode())
                    && modelRepository.existsByModelCode(newCode)) {
                throw new DataIntegrityViolationException("modelCode đã tồn tại");
            }
        }

        modelMapper.updateEntity(entity, req);
        // Entity đã được dirty-checking, không cần save lại trừ khi bạn tắt transactional.
        return modelMapper.toResponse(entity);
    }

    @Override
    public void delete(Long id) {
        if (!modelRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy model id=" + id);
        }
        modelRepository.deleteById(id);
    }
}
