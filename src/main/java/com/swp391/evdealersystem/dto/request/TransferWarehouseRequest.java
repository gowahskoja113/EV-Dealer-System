package com.swp391.evdealersystem.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferWarehouseRequest {
    @NotNull(message = "Phải chọn đại lý đích để nhận kho")
    private Long targetDealershipId;
}
