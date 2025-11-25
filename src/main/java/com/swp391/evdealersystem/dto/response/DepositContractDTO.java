package com.swp391.evdealersystem.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class DepositContractDTO {

    private String contractNumber;
    private LocalDate contractDate;

    // --- Bên B (Người Bán - Hardcoded) ---
    private String companyName;
    private String companyTaxCode;
    private String companyAddress;
    private String companyPhone;
    private String legalRepName;
    private String legalRepTitle;

    // --- Bên A (Người Mua) ---
    private String customerName;
    private String customerAddress;
    private String customerPhone;
    private String customerCitizenId; // Hard-coded "cho có"
    private String customerEmail;     // Hard-coded "cho có"

    // --- Tài sản (Xe) ---
    private String vehicleBrand;
    private String vehicleModelCode;
    private Integer vehicleProductionYear;
    private String vehicleColor;
    private String vehicleVin; // Số VIN
    private BigDecimal vehicleTotalPrice; // Giá niêm yết

    // --- Giao dịch ---
    private BigDecimal plannedDepositAmount;
    private String dealerShipName;
}