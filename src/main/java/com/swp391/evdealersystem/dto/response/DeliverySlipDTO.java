package com.swp391.evdealersystem.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

/**
 * DTO chứa dữ liệu "phẳng" để in Phiếu Giao Xe.
 */
@Getter
@Setter
@Builder
public class DeliverySlipDTO {

    // Thông tin phiếu
    private Long orderId;
    private LocalDate deliveryDate;
    private String salespersonName; // Tên nhân viên bán hàng

    // Thông tin khách
    private String customerName;
    private String customerAddress;
    private String customerPhone;

    // Thông tin xe
    private String vehicleBrand;
    private String vehicleModelCode;
    private String vehicleColor;
    private String vehicleVin;
}