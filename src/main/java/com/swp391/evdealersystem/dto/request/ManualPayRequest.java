package com.swp391.evdealersystem.dto.request;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ManualPayRequest {
    private Integer installmentSequence;
    private BigDecimal amount;
    private String note;
}
