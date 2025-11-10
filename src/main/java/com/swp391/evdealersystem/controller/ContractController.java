package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.UpdateDeliveryDateRequest;
import com.swp391.evdealersystem.dto.response.OrderResponse;
import com.swp391.evdealersystem.service.ContractService;
import com.swp391.evdealersystem.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;
    private final OrderService orderService;

    @GetMapping("/order/{orderId}/deposit-contract")
    public ResponseEntity<byte[]> getDepositContract(@PathVariable Long orderId) {

        byte[] pdfBytes = contractService.generateDepositContract(orderId);

        String filename = "HopDongCoc_" + orderId + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF); // Báo đây là file PDF
        headers.setContentDispositionFormData("inline", filename); // Yêu cầu trình duyệt 'hiển thị'

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfBytes);
    }
    @PutMapping("/{orderId}/delivery")
    public ResponseEntity<OrderResponse> updateDeliveryDate(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateDeliveryDateRequest request) {

        OrderResponse response = orderService.setDeliveryDate(orderId, request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{orderId}/delivery-slip")
    public ResponseEntity<byte[]> getDeliverySlip(@PathVariable Long orderId) {

        byte[] pdfBytes = orderService.generateDeliverySlip(orderId);
        String filename = "PhieuGiaoXe_" + orderId + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", filename);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfBytes);
    }
}