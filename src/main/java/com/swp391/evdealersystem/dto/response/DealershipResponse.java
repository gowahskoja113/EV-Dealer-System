package com.swp391.evdealersystem.dto.response;

import com.swp391.evdealersystem.enums.DealershipStatus;
import lombok.Data;
import java.util.List;

@Data
public class DealershipResponse {

    private Long dealershipId;
    private String name;
    private String address;
    private String phoneNumber;

    private DealershipStatus status;
    private List<WarehouseSummaryDTO> warehouses;
}