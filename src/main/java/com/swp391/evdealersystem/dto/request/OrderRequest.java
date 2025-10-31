package com.swp391.evdealersystem.dto.request;

import com.swp391.evdealersystem.enums.OrderPaymentStatus;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class OrderRequest {

    /**
     * Nếu không truyền -> mặc định PAID (đã thanh toán phần còn lại).
     * Có thể truyền OVERDUE để hủy do quá hạn (-> Order chuyển CANCELED).
     */
    private OrderPaymentStatus paymentStatus;

    /** optional: nếu muốn set ngày giao khi thanh toán xong */
    private LocalDate deliveryDate;
}
