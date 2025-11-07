package com.swp391.evdealersystem.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DealershipRequest {

    @NotEmpty(message = "Tên đại lý không được để trống")
    @Size(max = 255)
    private String name;

    @NotEmpty(message = "Địa chỉ không được để trống")
    @Size(max = 255)
    private String address;

    @Size(max = 20)
    private String phoneNumber;
}