package com.swp391.evdealersystem.dto.request;

import com.swp391.evdealersystem.entity.Customer;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
@Data
public class AppointmentRequest {

    @NotNull(message = "Customer ID is required")
    public Long customerId;  // ID của khách hàng

    @NotNull(message = "Service ID is required")
    public Long serviceId;  // ID của dịch vụ (lái thử, bảo dưỡng, v.v.)

    @NotNull(message = "Assigned User ID is required")
    public Long assignedUserId;  // ID của nhân viên phụ trách cuộc hẹn

    @NotNull(message = "Start time is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public LocalDateTime startAt;  // Thời gian bắt đầu cuộc hẹn

    @NotNull(message = "End time is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public LocalDateTime endAt;  // Thời gian kết thúc cuộc hẹn

    public String note;  // Ghi chú cho cuộc hẹn

}
