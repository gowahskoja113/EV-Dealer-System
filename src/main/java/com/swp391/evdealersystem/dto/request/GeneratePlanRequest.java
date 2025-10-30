package com.swp391.evdealersystem.dto.request;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GeneratePlanRequest {
    private BigDecimal depositAmount;   // tiền cọc
    private int regularInstallments;    // số kỳ sau cọc
    private String firstDueDate;        // yyyy-MM-dd
    private String period;              // ISO-8601: "P1M", "P15D"...
}