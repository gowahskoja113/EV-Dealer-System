package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.response.DeliverySlipDTO;
import com.swp391.evdealersystem.dto.response.DepositContractDTO;
import java.io.IOException;

/**
 * Service chuyên dụng để tạo file PDF từ các DTO dữ liệu.
 */
public interface PdfGenerationService {

    /**
     * Tạo file PDF Hợp đồng Đặt cọc.
     * @param dto Dữ liệu hợp đồng đã được chuẩn bị.
     * @return byte[] của file PDF.
     */
    byte[] generateDepositContractPdf(DepositContractDTO dto) throws IOException;

    /**
     * Tạo file PDF Phiếu Giao Xe.
     * @param dto Dữ liệu phiếu giao xe đã được chuẩn bị.
     * @return byte[] của file PDF.
     */
    byte[] generateDeliverySlipPdf(DeliverySlipDTO dto) throws IOException;
}