package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.response.VehicleSerialResponse;
import com.swp391.evdealersystem.entity.User;
import com.swp391.evdealersystem.entity.VehicleSerial;
import com.swp391.evdealersystem.exception.ResourceNotFoundException;
import com.swp391.evdealersystem.mapper.VehicleSerialMapper;
import com.swp391.evdealersystem.repository.UserRepository;
import com.swp391.evdealersystem.repository.VehicleSerialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections; // Import cái này để trả về list rỗng
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleSerialServiceImpl implements VehicleSerialService {

    private final VehicleSerialRepository vehicleSerialRepo;
    private final UserRepository userRepo;
    private final VehicleSerialMapper mapper;

    // Helper lấy User hiện tại
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleSerialResponse> getAllVehicleSerials() {
        User user = getCurrentUser();
        List<VehicleSerial> serials;

        // 1. ADMIN: Lấy hết
        if ("ROLE_ADMIN".equals(user.getRole().getRoleName())) {
            serials = vehicleSerialRepo.findAll();
        }
        // 2. STAFF: Chỉ lấy xe thuộc Dealer của mình
        else if (user.getDealership() != null) {
            Long dealerId = user.getDealership().getDealershipId();

            // Query này bạn đã có trong Repository rồi
            serials = vehicleSerialRepo.findByWarehouse_Dealership_DealershipId(dealerId);
        }
        // 3. KHÁCH / USER TỰ DO: Không thấy gì
        else {
            serials = Collections.emptyList();
        }

        return serials.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}