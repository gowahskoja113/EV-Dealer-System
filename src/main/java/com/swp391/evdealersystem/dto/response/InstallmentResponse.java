package com.swp391.evdealersystem.dto.response;

import com.swp391.evdealersystem.enums.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallmentResponse {
    private Long installmentId;
    private Integer sequence;
    private InstallmentType type;
    private String dueDate;
    private BigDecimal amountDue;
    private BigDecimal amountPaid;
    private BigDecimal lateFee;
    private InstallmentStatus status;
}
