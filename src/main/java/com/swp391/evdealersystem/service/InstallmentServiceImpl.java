// service/impl/InstallmentServiceImpl.java
package com.swp391.evdealersystem.service.impl;

import com.swp391.evdealersystem.dto.request.GeneratePlanRequest;
import com.swp391.evdealersystem.dto.response.InstallmentResponse;
import com.swp391.evdealersystem.entity.Installment;
import com.swp391.evdealersystem.entity.Order;
import com.swp391.evdealersystem.enums.*;
import com.swp391.evdealersystem.repository.InstallmentRepository;
import com.swp391.evdealersystem.repository.OrderRepository;
import com.swp391.evdealersystem.service.InstallmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstallmentServiceImpl implements InstallmentService {

    private final OrderRepository orderRepo;
    private final InstallmentRepository installmentRepo;

    @Transactional
    @Override
    public List<InstallmentResponse> generatePlan(Long orderId, GeneratePlanRequest req) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        BigDecimal total = order.getTotalAmount();
        BigDecimal deposit = req.getDepositAmount() == null ? BigDecimal.ZERO : req.getDepositAmount();
        if (deposit.signum() < 0 || deposit.compareTo(total) > 0)
            throw new IllegalArgumentException("Deposit invalid");

        int n = req.getRegularInstallments();
        BigDecimal remaining = total.subtract(deposit);
        if (remaining.signum() > 0 && n <= 0)
            throw new IllegalArgumentException("regularInstallments must be > 0");

        LocalDate firstDue = LocalDate.parse(req.getFirstDueDate());
        Period step = (req.getPeriod() == null) ? Period.ofMonths(1) : Period.parse(req.getPeriod());

        // Xoá kế hoạch cũ nếu có
        List<Installment> old = installmentRepo.findByOrderOrderIdOrderBySequenceAsc(orderId);
        installmentRepo.deleteAll(old);

        List<Installment> toSave = new ArrayList<>();

        // Kỳ đặt cọc (seq 0)
        toSave.add(Installment.builder()
                .order(order).sequence(0).type(InstallmentType.DEPOSIT)
                .dueDate(LocalDate.now())
                .amountDue(deposit).amountPaid(BigDecimal.ZERO)
                .lateFee(BigDecimal.ZERO).status(InstallmentStatus.PENDING)
                .build());

        // Chia đều phần còn lại vào REGULAR 1..n
        if (n > 0) {
            BigDecimal base = remaining.divide(BigDecimal.valueOf(n), 0, java.math.RoundingMode.DOWN);
            BigDecimal sumBase = base.multiply(BigDecimal.valueOf(n));
            BigDecimal rem    = remaining.subtract(sumBase); // 0..(n-1)

            LocalDate due = firstDue;
            for (int i = 1; i <= n; i++) {
                BigDecimal amt = base.add(i <= rem.intValue() ? BigDecimal.ONE : BigDecimal.ZERO);
                toSave.add(Installment.builder()
                        .order(order).sequence(i).type(InstallmentType.REGULAR)
                        .dueDate(due)
                        .amountDue(amt).amountPaid(BigDecimal.ZERO)
                        .lateFee(BigDecimal.ZERO)
                        .status(amt.signum()==0 ? InstallmentStatus.PAID : InstallmentStatus.PENDING)
                        .build());
                due = due.plus(step);
            }
        }

        installmentRepo.saveAll(toSave);
        order.setDepositAmount(deposit);
        order.setPaymentStatus(remaining.signum()==0 ? OrderPaymentStatus.PAID : OrderPaymentStatus.UNPAID);
        orderRepo.save(order);

        return toSave.stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public List<InstallmentResponse> listByOrder(Long orderId) {
        return installmentRepo.findByOrderOrderIdOrderBySequenceAsc(orderId)
                .stream().map(this::map).collect(Collectors.toList());
    }

    private InstallmentResponse map(Installment i) {
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
