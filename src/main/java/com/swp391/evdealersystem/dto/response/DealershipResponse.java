package com.swp391.evdealersystem.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class DealershipResponse {

    private Long dealershipId;
    private String name;
    private String address;
    private String phoneNumber;

    private List<WarehouseSummaryDTO> warehouses;
}