package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.response.DepositContractDTO;
import com.swp391.evdealersystem.entity.*;
import com.swp391.evdealersystem.enums.OrderPaymentStatus;
import com.swp391.evdealersystem.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor; // Đảm bảo đã import
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException; // Cần import

@Service
@RequiredArgsConstructor // Dùng cái này
public class ContractServiceImpl implements ContractService {

    private final OrderRepository orderRepo;
    private final PdfGenerationService pdfGenerationService; // Inject service mới

    // === Đọc thông tin Bên B từ application.properties ===
    // (Lưu ý: Bạn có thể giữ lại các @Value ở đây, hoặc chuyển hẳn sang PdfGenerationServiceImpl)
    // (Tôi đã chuyển chúng sang PdfGenerationServiceImpl, nên xóa ở đây đi)

    // XÓA HẾT CÁC @Value ở đây
    // XÓA HẾT CÁC HẰNG SỐ FORMATTER ở đây

    @Override
    @Transactional(readOnly = true)
    public byte[] generateDepositContract(Long orderId) {

        Order order = orderRepo.findOrderDetailsForContract(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        if (order.getPaymentStatus() == OrderPaymentStatus.UNPAID) {
            throw new IllegalStateException("Order " + orderId + " has not been paid for the deposit. Cannot print contract.");
        }

        // 1. Map dữ liệu
        DepositContractDTO dto = mapOrderToContractDTO(order);

        // 2. Gọi service PDF
        try {
            return pdfGenerationService.generateDepositContractPdf(dto);
        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF contract: " + e.getMessage(), e);
        }
    }

    /**
     * Hàm map này vẫn giữ nguyên, nó chuẩn bị DTO
     */
    private DepositContractDTO mapOrderToContractDTO(Order order) {
        Customer c = order.getCustomer();
        VehicleSerial s = order.getSerial();
        ElectricVehicle v = s.getVehicle();
        Model m = v.getModel();

        return DepositContractDTO.builder()
                // Thông tin Hợp đồng
                .contractNumber(String.valueOf(order.getOrderId()))
                .contractDate(order.getOrderDate().toLocalDate())

                // Bên A
                .customerName(c.getName())
                .customerAddress(c.getAddress())
                .customerPhone(c.getPhoneNumber())
                .customerCitizenId("001099001234")
                .customerEmail("customer.demo@example.com")

                // Xe
                .vehicleBrand(m.getBrand())
                .vehicleModelCode(m.getModelCode())
                .vehicleProductionYear(m.getProductionYear())
                .vehicleColor(m.getColor())
                .vehicleVin(s.getVin())
                .vehicleTotalPrice(v.getPrice())

                // Tiền
                .plannedDepositAmount(order.getPlannedDepositAmount())
                .build();
    }
}