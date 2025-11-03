package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.enums.VehicleStatus;
import com.swp391.evdealersystem.repository.ElectricVehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// import java.time.OffsetDateTime; // Bỏ import không dùng
// import java.time.temporal.ChronoUnit; // Bỏ import không dùng

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleStatusServiceImpl implements VehicleStatusService {

    private final ElectricVehicleRepository repo;

    // Đã bỏ phương thức placeHold(Long vehicleId, long holdMinutes)
    // vì nó phụ thuộc vào holdUntil và trạng thái HOLD

    // Đã bỏ phương thức releaseHoldIfExpired(Long vehicleId)
    // vì nó phụ thuộc vào holdUntil và trạng thái HOLD

    @Override
    @Transactional
    public ElectricVehicle markSoldOut(Long vehicleId) {
        ElectricVehicle ev = repo.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId));

        ev.setStatus(VehicleStatus.SOLD_OUT);
        // ev.setHoldUntil(null); // Đã bỏ
        ElectricVehicle saved = repo.save(ev);
        log.debug("Vehicle {} marked as SOLD_OUT", saved.getVehicleId());
        return saved;
    }

    @Override
    @Transactional
    public ElectricVehicle markAvailable(Long vehicleId) {
        ElectricVehicle ev = repo.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId));

        ev.setStatus(VehicleStatus.AVAILABLE);
        // ev.setHoldUntil(null); // Đã bỏ
        ElectricVehicle saved = repo.save(ev);
        log.debug("Vehicle {} marked as AVAILABLE", saved.getVehicleId());
        return saved;
    }
}