package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.DealershipRequest;
import com.swp391.evdealersystem.dto.response.DealershipResponse;
import com.swp391.evdealersystem.dto.response.WarehouseSummaryDTO;
import com.swp391.evdealersystem.entity.Dealership;
import com.swp391.evdealersystem.entity.Warehouse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DealershipMapper {

    // 1. Dùng cho việc TẠO MỚI (Create)
    public Dealership toEntity(DealershipRequest request) {
        if (request == null) {
            return null;
        }

        Dealership dealership = new Dealership();
        dealership.setName(request.getName());
        dealership.setAddress(request.getAddress());
        dealership.setPhoneNumber(request.getPhoneNumber());
        return dealership;
    }

    public void toUpdate(DealershipRequest request, Dealership dealership) {
        if (request == null || dealership == null) {
            return;
        }

        dealership.setName(request.getName());
        dealership.setAddress(request.getAddress());
        dealership.setPhoneNumber(request.getPhoneNumber());
    }

    // 3. Dùng cho việc TRẢ VỀ (Response)
    public DealershipResponse toResponse(Dealership dealership) {
        if (dealership == null) {
            return null;
        }

        DealershipResponse response = new DealershipResponse();
        response.setDealershipId(dealership.getDealershipId());
        response.setName(dealership.getName());
        response.setAddress(dealership.getAddress());
        response.setPhoneNumber(dealership.getPhoneNumber());

        // Chuyển đổi danh sách Warehouse (Entity) sang WarehouseResponseDTO
        if (dealership.getWarehouses() != null) {
            response.setWarehouses(
                    dealership.getWarehouses()
                            .stream()
                            .map(this::toWarehouseResponseDTO) // Gọi hàm helper
                            .collect(Collectors.toList())
            );
        }

        return response;
    }

    private WarehouseSummaryDTO toWarehouseResponseDTO(Warehouse warehouse) {
        if (warehouse == null) {
            return null;
        }

        WarehouseSummaryDTO dto = new WarehouseSummaryDTO();
        dto.setWarehouseId(warehouse.getWarehouseId());
        dto.setWarehouseLocation(warehouse.getWarehouseLocation());
        dto.setWarehouseName(warehouse.getWarehouseName());
        dto.setVehicleQuantity(warehouse.getVehicleQuantity());
        return dto;
    }
}