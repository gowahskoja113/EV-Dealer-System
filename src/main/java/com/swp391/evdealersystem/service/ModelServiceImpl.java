package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.ModelRequest;
import com.swp391.evdealersystem.dto.response.ModelResponse;
import com.swp391.evdealersystem.entity.Model;
import com.swp391.evdealersystem.mapper.ModelMapper;
import com.swp391.evdealersystem.repository.ElectricVehicleRepository;
import com.swp391.evdealersystem.repository.ModelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ModelServiceImpl implements ModelService {

    private final ElectricVehicleRepository evRepo;
    private final ModelRepository modelRepository;
    private final ModelMapper mapper;
    private final ModelRepository modelRepo;

    @Override
    @Transactional
    public ModelResponse create(ModelRequest req) {
        if (modelRepo.existsByModelCode(req.getModelCode())) {
            throw new IllegalArgumentException("Model code already exists: " + req.getModelCode());
        }
        Model model = mapper.toEntity(req);
        model = modelRepo.save(model);
        return mapper.toResponse(model);
    }

    @Override
    public ModelResponse getById(Long id) {
        Model model = modelRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + id));
        return mapper.toResponse(model);
    }

    @Override
    public ModelResponse getByCode(String modelCode) {
        Model entity = modelRepository.findByModelCode(modelCode)
                .orElseThrow(() -> new IllegalArgumentException("Cant not found modelCode = " + modelCode));
        return mapper.toResponse(entity);
    }

    @Override
    public List<ModelResponse> list(Pageable pageable) {
        return modelRepo.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    public ModelResponse update(Long id, ModelRequest req) {
        Model model = modelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + id));

        // update model code
        if (req != null && req.getModelCode() != null) {
            String newCode = req.getModelCode().trim();
            if (!newCode.isEmpty() && !newCode.equals(model.getModelCode())
                    && modelRepository.existsByModelCode(newCode)) {
                throw new DataIntegrityViolationException( newCode + "is already exists: ");
            }
        }

        //update brand
        if (req != null && req.getBrand() != null) {
            model.setBrand(req.getBrand());
        }
        return mapper.toResponse(model);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Model model = modelRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + id));
        long count = evRepo.countByModel(model);
        if (count > 0) {
            throw new IllegalStateException("Cannot delete model while it still has vehicles (" + count + ")");
        }
        modelRepo.delete(model);
    }
}
