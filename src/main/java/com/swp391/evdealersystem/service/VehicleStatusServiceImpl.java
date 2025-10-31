package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.enums.VehicleStatus;
import com.swp391.evdealersystem.repository.ElectricVehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleStatusServiceImpl implements VehicleStatusService {

    private final ElectricVehicleRepository repo;

    @Override
    @Transactional
    public ElectricVehicle placeHold(Long vehicleId, long holdMinutes) {
        ElectricVehicle ev = repo.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId));

        // neu xe da ban het thi khong duoc dat coc
        if (ev.getStatus() == VehicleStatus.SOLD_OUT) {
            throw new IllegalStateException("Vehicle is sold out");
        }
        // neu dang hole va chua het han thi khong duoc dat
        if (ev.getStatus() == VehicleStatus.HOLD
                && ev.getHoldUntil() != null
                && OffsetDateTime.now().isBefore(ev.getHoldUntil())) {
            throw new IllegalStateException("Vehicle is already on hold");
        }

        // Đặt hold
        ev.setStatus(VehicleStatus.HOLD);
        ev.setHoldUntil(OffsetDateTime.now().plus(holdMinutes, ChronoUnit.MINUTES));
        ElectricVehicle saved = repo.save(ev);
        log.debug("Vehicle {} put on HOLD until {}", saved.getVehicleId(), saved.getHoldUntil());
        return saved;
    }

    @Override
    @Transactional
    public ElectricVehicle releaseHoldIfExpired(Long vehicleId) {
        ElectricVehicle ev = repo.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId));

        // Chỉ nhả hold khi đã hết hạn hoặc không có hạn
        if (ev.getStatus() == VehicleStatus.HOLD
                && (ev.getHoldUntil() == null || OffsetDateTime.now().isAfter(ev.getHoldUntil()))) {
            ev.setStatus(VehicleStatus.AVAILABLE);
            ev.setHoldUntil(null);
            ElectricVehicle saved = repo.save(ev);
            log.debug("Vehicle {} HOLD released -> AVAILABLE", saved.getVehicleId());
            return saved;
        }
        return ev;
    }

    @Override
    @Transactional
    public ElectricVehicle markSoldOut(Long vehicleId) {
        ElectricVehicle ev = repo.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId));

        ev.setStatus(VehicleStatus.SOLD_OUT);
        ev.setHoldUntil(null);
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
        ev.setHoldUntil(null);
        ElectricVehicle saved = repo.save(ev);
        log.debug("Vehicle {} marked as AVAILABLE", saved.getVehicleId());
        return saved;
    }
}
