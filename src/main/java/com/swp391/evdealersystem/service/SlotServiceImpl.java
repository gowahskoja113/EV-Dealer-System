package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.entity.Slot;
import com.swp391.evdealersystem.repository.SlotRepository;
import com.swp391.evdealersystem.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SlotServiceImpl implements SlotService {

    @Autowired
    private SlotRepository slotRepository;

    // Quản lý Slot

    @Override
    public Slot createSlot(Slot slot) {
        return slotRepository.save(slot);
    }

    @Override
    public List<Slot> getSlotsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return slotRepository.findByStartTimeBetween(startTime, endTime);
    }

    @Override
    public Slot getSlotById(Long slotId) {
        return slotRepository.findById(slotId).orElse(null);
    }

    @Override
    public Slot updateSlot(Long slotId, Slot updatedSlot) {
        Optional<Slot> existingSlot = slotRepository.findById(slotId);
        if (existingSlot.isPresent()) {
            Slot slot = existingSlot.get();
            slot.setStartTime(updatedSlot.getStartTime());
            slot.setEndTime(updatedSlot.getEndTime());
            slot.setMaxTestDrive(updatedSlot.getMaxTestDrive());
            slot.setMaxService(updatedSlot.getMaxService());
            slot.setTestDriveCount(updatedSlot.getTestDriveCount());
            slot.setServiceCount(updatedSlot.getServiceCount());
            return slotRepository.save(slot);
        }
        return null;
    }

    @Override
    public void deleteSlot(Long slotId) {
        slotRepository.deleteById(slotId);
    }

    @Override
    public boolean isSlotAvailableForTestDrive(Long slotId) {
        return false;
    }

    @Override
    public boolean isSlotAvailableForService(Long slotId) {
        return false;
    }

//    // Kiểm tra tình trạng Slot
//
//    @Override
//    public boolean isSlotAvailableForTestDrive(Long slotId) {
//        Optional<Slot> slotOptional = slotRepository.findById(slotId);
//        if (slotOptional.isPresent()) {
//            Slot slot = slotOptional.get();
//            return !slot.isFullForTestDrive();
//        }
//        return false;
//    }
//
//    @Override
//    public boolean isSlotAvailableForService(Long slotId) {
//        Optional<Slot> slotOptional = slotRepository.findById(slotId);
//        if (slotOptional.isPresent()) {
//            Slot slot = slotOptional.get();
//            return !slot.isFullForService();
//        }
//        return false;
//    }
}
