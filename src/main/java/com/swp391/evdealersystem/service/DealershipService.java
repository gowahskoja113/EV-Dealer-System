package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.DealershipRequest;
import com.swp391.evdealersystem.dto.response.DealershipResponse;
import com.swp391.evdealersystem.enums.DealershipStatus;

import java.util.List;

public interface DealershipService {
    DealershipResponse createDealership(DealershipRequest dealershipRequest);
    DealershipResponse getDealershipById(Long id);
    List<DealershipResponse> getAllDealerships();
    DealershipResponse updateDealership(Long id, DealershipRequest dealershipRequest);
    void deleteDealership(Long id);
    public DealershipResponse changeStatus(Long id, DealershipStatus newStatus);
    public void transferAllWarehouses(Long sourceDealershipId, Long targetDealershipId);
    void deleteWarehouseFromDealership(Long dealershipId, Long warehouseId);
}