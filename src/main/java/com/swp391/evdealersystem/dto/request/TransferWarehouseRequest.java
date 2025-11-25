package com.swp391.evdealersystem.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class TransferWarehouseRequest {
    @NotNull(message = "Phải chọn đại lý đích để nhận kho")
    private Long targetDealershipId;
    private List<Long> warehouseIds;
}
