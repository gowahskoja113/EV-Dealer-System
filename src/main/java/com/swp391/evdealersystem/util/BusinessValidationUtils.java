package com.swp391.evdealersystem.util;

import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.User;
import com.swp391.evdealersystem.entity.Warehouse;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;

public class BusinessValidationUtils {

    private BusinessValidationUtils() {}

    public static void validateDeposit(ElectricVehicle v, BigDecimal deposit) {
        if (v == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }
        if (v.getPrice() == null || v.getPrice().signum() < 0) {
            throw new IllegalArgumentException("Invalid vehicle price: price must be >= 0");
        }
        if (deposit == null || deposit.signum() < 0) {
            throw new IllegalArgumentException("Deposit amount cannot be negative");
        }
        if (deposit.compareTo(v.getPrice()) > 0) {
            throw new IllegalArgumentException("Deposit amount (" + deposit + ") cannot be greater than vehicle price (" + v.getPrice() + ")");
        }
    }

    public static void validateWarehouseAccess(Warehouse w, User user) {
        // 1. Admin quyền lực tối cao -> Cho qua
        if (user.getRole().getRoleName().equalsIgnoreCase("ROLE_ADMIN")) {
            return;
        }

        // 2. Nếu User không thuộc Dealership nào (Khách hàng/User tự do) -> CHẶN
        if (user.getDealership() == null) {
            throw new AccessDeniedException("Bạn không có quyền truy cập hoặc chỉnh sửa kho hàng.");
        }

        // 3. Nếu User có Dealership -> So sánh ID Dealer của User và của Kho
        Long userDealerId = user.getDealership().getDealershipId();
        Long warehouseDealerId = w.getDealership().getDealershipId();

        if (!userDealerId.equals(warehouseDealerId)) {
            throw new AccessDeniedException("Bạn không có quyền thao tác trên kho của Đại lý khác!");
        }
    }


}