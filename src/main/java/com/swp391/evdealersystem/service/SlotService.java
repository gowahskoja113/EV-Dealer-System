package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.entity.Slot;
import java.time.LocalDateTime;
import java.util.List;

public interface SlotService {

    // Quản lý Slot
    Slot createSlot(Slot slot);
    List<Slot> getSlotsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    Slot getSlotById(Long slotId);
    Slot updateSlot(Long slotId, Slot updatedSlot);
    void deleteSlot(Long slotId);

    // Kiểm tra tình trạng Slot
    boolean isSlotAvailableForTestDrive(Long slotId);
    boolean isSlotAvailableForService(Long slotId);
}
