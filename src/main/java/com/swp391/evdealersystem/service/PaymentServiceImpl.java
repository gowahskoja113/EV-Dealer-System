//package com.swp391.evdealersystem.service;
//
//import com.swp391.evdealersystem.entity.Order;
//import com.swp391.evdealersystem.enums.OrderPaymentStatus;
//import com.swp391.evdealersystem.repository.OrderRepository;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class PaymentServiceImpl implements PaymentService {
//
//    private final OrderRepository orderRepo;
//    private final InstallmentRepository installmentRepo;
//
//    @Transactional
//    @Override
//    public void manualPay(Long orderId, ManualPayRequest req) {
//        Order order = orderRepo.findById(orderId)
//                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));
//
//        BigDecimal remaining = req.getAmount();
//        if (remaining == null || remaining.signum() <= 0) return;
//
//        List<Installment> list = installmentRepo.findByOrderOrderIdOrderBySequenceAsc(orderId);
//
//        if (req.getInstallmentSequence() != null) {
//            // Cộng vào kỳ cụ thể
//            Installment ins = list.stream()
//                    .filter(i -> i.getSequence().equals(req.getInstallmentSequence()))
//                    .findFirst()
//                    .orElseThrow(() -> new EntityNotFoundException("Installment not found seq=" + req.getInstallmentSequence()));
//            applyToOne(ins, remaining);
//        } else {
//            // FIFO: dồn từ kỳ thấp nhất chưa đủ
//            for (Installment ins : list) {
//                if (remaining.signum() <= 0) break;
//                remaining = applyPartially(ins, remaining);
//            }
//        }
//
//        // cap nhat trang thai order
//        BigDecimal totalPaid = list.stream()
//                .map(Installment::getAmountPaid)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        int cmp = totalPaid.compareTo(order.getTotalAmount());
//        if (cmp == 0) order.setPaymentStatus(OrderPaymentStatus.PAID);
//        else if (cmp > 0) order.setPaymentStatus(OrderPaymentStatus.OVERPAID);
//        else if (totalPaid.signum() > 0) order.setPaymentStatus(OrderPaymentStatus.PARTIALLY_PAID);
//        else order.setPaymentStatus(OrderPaymentStatus.UNPAID);
//
//        orderRepo.save(order);
//    }
//
//    private void applyToOne(Installment ins, BigDecimal amount) {
//        BigDecimal need = ins.getAmountDue().add(ins.getLateFee()).subtract(ins.getAmountPaid());
//        if (need.signum() <= 0) {
//            ins.setStatus(InstallmentStatus.PAID);
//            installmentRepo.save(ins);
//            return;
//        }
//        BigDecimal alloc = amount.min(need);
//        ins.setAmountPaid(ins.getAmountPaid().add(alloc));
//        updateStatus(ins);
//    }
//
//    private BigDecimal applyPartially(Installment ins, BigDecimal remaining) {
//        BigDecimal need = ins.getAmountDue().add(ins.getLateFee()).subtract(ins.getAmountPaid());
//        if (need.signum() <= 0) {
//            ins.setStatus(InstallmentStatus.PAID);
//            installmentRepo.save(ins);
//            return remaining;
//        }
//        BigDecimal alloc = remaining.min(need);
//        ins.setAmountPaid(ins.getAmountPaid().add(alloc));
//        updateStatus(ins);
//        return remaining.subtract(alloc);
//    }
//
//    private void updateStatus(Installment ins) {
//        BigDecimal target = ins.getAmountDue().add(ins.getLateFee());
//        int cmp = ins.getAmountPaid().compareTo(target);
//        if (cmp >= 0) ins.setStatus(InstallmentStatus.PAID);
//        else if (ins.getAmountPaid().signum() > 0) ins.setStatus(InstallmentStatus.PARTIALLY_PAID);
//        installmentRepo.save(ins);
//    }
//}
