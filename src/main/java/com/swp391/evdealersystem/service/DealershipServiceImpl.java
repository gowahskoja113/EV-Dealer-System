package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.DealershipRequest;
import com.swp391.evdealersystem.dto.response.DealershipResponse;
import com.swp391.evdealersystem.entity.Dealership;
import com.swp391.evdealersystem.entity.Warehouse;
import com.swp391.evdealersystem.exception.ResourceNotFoundException;
import com.swp391.evdealersystem.mapper.DealershipMapper;
import com.swp391.evdealersystem.repository.DealershipRepository;
import com.swp391.evdealersystem.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealershipServiceImpl implements DealershipService {

    private final DealershipRepository dealershipRepository;
    private final DealershipMapper dealershipMapper;
    private final WarehouseRepository warehouseRepository;

    @Override
    @Transactional
    public DealershipResponse createDealership(DealershipRequest dealershipRequest) {
        Dealership dealership = dealershipMapper.toEntity(dealershipRequest);
        Dealership savedDealership = dealershipRepository.save(dealership);
        return dealershipMapper.toResponse(savedDealership);
    }

    @Override
    @Transactional(readOnly = true) // Chỉ đọc
    public DealershipResponse getDealershipById(Long id) {
        Dealership dealership = dealershipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đại lý với ID: " + id));
        return dealershipMapper.toResponse(dealership);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DealershipResponse> getAllDealerships() {
        List<Dealership> dealerships = dealershipRepository.findAll();
        return dealerships.stream()
                .map(dealershipMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DealershipResponse updateDealership(Long id, DealershipRequest dealershipRequest) {
        Dealership existingDealership = dealershipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đại lý với ID: " + id));

        // Cập nhật các trường từ DTO vào entity đã tồn tại
        dealershipMapper.toUpdate(dealershipRequest, existingDealership);

        Dealership updatedDealership = dealershipRepository.save(existingDealership);
        return dealershipMapper.toResponse(updatedDealership);
    }

    @Override
    @Transactional
    public void deleteDealership(Long id) {
        Dealership dealership = dealershipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đại lý với ID: " + id));
        dealershipRepository.delete(dealership);
    }

    @Override
    @Transactional
    public void deleteWarehouseFromDealership(Long dealershipId, Long warehouseId) {
        // 1. Kiểm tra Dealership có tồn tại không
        if (!dealershipRepository.existsById(dealershipId)) {
            throw new ResourceNotFoundException("Không tìm thấy đại lý với ID: " + dealershipId);
        }

        // 2. Tìm kho
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kho với ID: " + warehouseId));

        if (!warehouse.getDealership().getDealershipId().equals(dealershipId)) {
            throw new IllegalArgumentException("Lỗi: Kho (ID: " + warehouseId + ") không thuộc về Đại lý (ID: " + dealershipId + ")");
        }

        warehouseRepository.delete(warehouse);
    }
}