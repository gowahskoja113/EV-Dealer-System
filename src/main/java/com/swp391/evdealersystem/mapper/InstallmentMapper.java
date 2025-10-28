package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.response.InstallmentResponse;
import com.swp391.evdealersystem.entity.Installment;
import org.springframework.stereotype.Component;

@Component
public class InstallmentMapper {

    public InstallmentResponse toResponse(Installment i) {
        if (i == null) return null;
        return InstallmentResponse.builder()
                .installmentId(i.getInstallmentId())
                .sequence(i.getSequence())
                .type(i.getType())
                .dueDate(i.getDueDate().toString())
                .amountDue(i.getAmountDue())
                .amountPaid(i.getAmountPaid())
                .lateFee(i.getLateFee())
                .status(i.getStatus())
                .build();
    }
}
