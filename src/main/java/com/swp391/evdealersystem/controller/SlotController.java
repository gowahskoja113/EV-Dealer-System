package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.entity.Slot;
import com.swp391.evdealersystem.service.SlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;

    /**
     * Tạo slot mới
     */
    @PostMapping
    public Slot createSlot(@Valid @RequestBody Slot slot) {
        return slotService.createSlot(slot);
    }

    /**
     * Lấy tất cả slot
     */
    @GetMapping
    public List<Slot> getAllSlots() {
        return slotService.getSlotsByTimeRange(
                LocalDateTime.MIN.plusYears(2000),
                LocalDateTime.MAX.minusYears(2000)
        );
    }

    /**
     * Lấy slot theo id
     */
    @GetMapping("/{id}")
    public Slot getSlotById(@PathVariable Long id) {
        return slotService.getSlotById(id);
    }

    /**
     * Lấy danh sách slot theo khoảng thời gian
     */
    @GetMapping("/range")
    public List<Slot> getSlotsByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    ) {
        return slotService.getSlotsByTimeRange(startTime, endTime);
    }

    /**
     * Cập nhật slot
     */
    @PutMapping("/{id}")
    public Slot updateSlot(
            @PathVariable Long id,
            @Valid @RequestBody Slot updatedSlot
    ) {
        return slotService.updateSlot(id, updatedSlot);
    }

    /**
     * Xóa slot
     */
    @DeleteMapping("/{id}")
    public void deleteSlot(@PathVariable Long id) {
        slotService.deleteSlot(id);
    }

    /**
     * Check còn chỗ lái thử không
     */
    @GetMapping("/{id}/available-testdrive")
    public boolean availableForTestDrive(@PathVariable Long id) {
        return slotService.isSlotAvailableForTestDrive(id);
    }

    /**
     * Check còn chỗ bảo dưỡng không
     */
    @GetMapping("/{id}/available-service")
    public boolean availableForService(@PathVariable Long id) {
        return slotService.isSlotAvailableForService(id);
    }
}
