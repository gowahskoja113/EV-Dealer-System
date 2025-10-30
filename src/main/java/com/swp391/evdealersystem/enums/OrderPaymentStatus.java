package com.swp391.evdealersystem.enums;

/**
 * Trạng thái thanh toán của phần còn lại sau khi trừ tiền cọc.
 * Dùng để xác định logic cập nhật trạng thái đơn hàng trong Order entity.
 */
public enum OrderPaymentStatus {
    /**
     * Chưa thanh toán phần còn lại.
     */
    UNPAID,

    /**
     * Đã thanh toán cọc (chưa hết phần còn lại).
     */
    DEPOSIT_PAID,

    /**
     * Đã thanh toán đủ phần còn lại (đơn hàng hoàn tất).
     */
    PAID,

    /**
     * Quá hạn thanh toán, dẫn đến đơn hàng bị hủy.
     */
    OVERDUE
}
