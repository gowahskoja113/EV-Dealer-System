package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.DealershipRequest;
import com.swp391.evdealersystem.dto.response.DealershipResponse;
import com.swp391.evdealersystem.entity.Dealership;
import com.swp391.evdealersystem.entity.Warehouse;
import com.swp391.evdealersystem.enums.DealershipStatus;
import com.swp391.evdealersystem.exception.ResourceNotFoundException;
import com.swp391.evdealersystem.mapper.DealershipMapper;
import com.swp391.evdealersystem.repository.DealershipRepository;
import com.swp391.evdealersystem.repository.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
    @Transactional(readOnly = true)
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

        if (dealership.getStatus() != DealershipStatus.INACTIVE) {
            throw new IllegalStateException("Không thể xóa đại lý đang hoạt động (ACTIVE). Hãy chuyển sang INACTIVE trước.");
        }

        if (!dealership.getWarehouses().isEmpty()) {
            throw new IllegalStateException("Không thể xóa đại lý vì vẫn còn dữ liệu kho liên kết.");
        }

        dealershipRepository.delete(dealership);
    }

    @Override
    @Transactional
    public void transferSelectedWarehouses(Long sourceDealershipId, Long targetDealershipId, List<Long> warehouseIds) {
        // 1. Validate cơ bản
        if (sourceDealershipId.equals(targetDealershipId)) {
            throw new IllegalArgumentException("Không thể chuyển kho cho chính đại lý đó.");
        }

        if (warehouseIds == null || warehouseIds.isEmpty()) {
            throw new IllegalArgumentException("Danh sách kho cần chuyển không được để trống.");
        }

        // 2. Lấy thông tin 2 đại lý
        Dealership source = dealershipRepository.findById(sourceDealershipId)
                .orElseThrow(() -> new EntityNotFoundException("Đại lý nguồn không tồn tại"));

        Dealership target = dealershipRepository.findById(targetDealershipId)
                .orElseThrow(() -> new EntityNotFoundException("Đại lý đích không tồn tại"));

        if (target.getStatus() == DealershipStatus.INACTIVE) {
            throw new IllegalStateException("Đại lý đích đang INACTIVE, không thể nhận kho.");
        }

        // 3. Tìm các kho dựa trên list ID được gửi lên
        List<Warehouse> warehousesToTransfer = warehouseRepository.findAllById(warehouseIds);

        // Kiểm tra xem số lượng kho tìm được có khớp với số lượng ID gửi lên không
        if (warehousesToTransfer.size() != warehouseIds.size()) {
            throw new EntityNotFoundException("Một số ID kho không tồn tại trong hệ thống.");
        }

        // 4. Xử lý logic chuyển đổi
        for (Warehouse w : warehousesToTransfer) {
            // QUAN TRỌNG: Kiểm tra xem kho này có thực sự thuộc về Dealership nguồn không
            if (!w.getDealership().getDealershipId().equals(sourceDealershipId)) {
                throw new AccessDeniedException(
                        "Kho có ID " + w.getWarehouseId() + " không thuộc về đại lý nguồn (ID: " + sourceDealershipId + "). Hủy giao dịch."
                );
            }

            // Cập nhật chủ sở hữu mới
            w.setDealership(target);
        }

        // 5. Lưu xuống DB
        // Vì Warehouse là bên nắm giữ khóa ngoại (Owning side), chỉ cần save Warehouse là đủ.
        warehouseRepository.saveAll(warehousesToTransfer);
    }

    @Override
    @Transactional
    public DealershipResponse changeStatus(Long id, DealershipStatus newStatus) {
        Dealership dealership = dealershipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đại lý"));

        if (newStatus == DealershipStatus.INACTIVE) {
            if (dealership.getWarehouses() != null && !dealership.getWarehouses().isEmpty()) {
                throw new IllegalStateException(
                        "Không thể ngừng hoạt động (INACTIVE). Đại lý này vẫn còn "
                                + dealership.getWarehouses().size()
                                + " kho hàng. Vui lòng chuyển giao kho sang đại lý khác trước.");
            }
        }

        dealership.setStatus(newStatus);
        return dealershipMapper.toResponse(dealershipRepository.save(dealership));
    }

    @Override
    @Transactional
    public void deleteWarehouseFromDealership(Long dealershipId, Long warehouseId) {
        if (!dealershipRepository.existsById(dealershipId)) {
            throw new ResourceNotFoundException("Không tìm thấy đại lý với ID: " + dealershipId);
        }

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kho với ID: " + warehouseId));

        if (!warehouse.getDealership().getDealershipId().equals(dealershipId)) {
            throw new IllegalArgumentException("Lỗi: Kho (ID: " + warehouseId + ") không thuộc về Đại lý (ID: " + dealershipId + ")");
        }

        warehouseRepository.delete(warehouse);
    }
}