package com.swp391.evdealersystem.service;

public interface ContractService {

    /**
     * Tạo file PDF Hợp đồng Đặt cọc cho một Order.
     *
     * @param orderId ID của Order.
     * @return Dữ liệu byte[] của file PDF.
     * @throws jakarta.persistence.EntityNotFoundException nếu không tìm thấy Order.
     * @throws IllegalStateException nếu Order chưa thanh toán cọc.
     */
    byte[] generateDepositContract(Long orderId);
}