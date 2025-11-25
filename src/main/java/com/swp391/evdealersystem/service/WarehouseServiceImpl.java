package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.TransferStockRequest;
import com.swp391.evdealersystem.dto.request.WarehouseRequest;
import com.swp391.evdealersystem.dto.request.WarehouseStockRequest;
import com.swp391.evdealersystem.dto.response.VehicleSerialResponse;
import com.swp391.evdealersystem.dto.response.WarehouseResponse;
import com.swp391.evdealersystem.dto.response.WarehouseStockFlat;
import com.swp391.evdealersystem.dto.response.WarehouseStockResponse;
import com.swp391.evdealersystem.entity.*;
import com.swp391.evdealersystem.enums.DealershipStatus;
import com.swp391.evdealersystem.enums.QtyMode;
import com.swp391.evdealersystem.enums.VehicleStatus;
import com.swp391.evdealersystem.mapper.WarehouseMapper;
import com.swp391.evdealersystem.repository.*;
import com.swp391.evdealersystem.util.VinGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepo;
    private final WarehouseStockRepository stockRepo;
    private final ElectricVehicleRepository vehicleRepo;
    private final WarehouseMapper mapper;
    private final ModelRepository modelRepository;
    private final VehicleSerialRepository vehicleSerialRepository;
    private final VinGenerator vinGenerator;
    private final DealershipRepository dealershipRepository;
    private final UserRepository userRepository;

    public User getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private void validateWarehouseAccess(Warehouse w, User user) {
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

    @Override
    @Transactional
    public WarehouseResponse create(WarehouseRequest request) {

        if (warehouseRepo.existsByWarehouseLocation(request.getWarehouseLocation())) {
            throw new IllegalArgumentException("Warehouse location already exists");
        }

        Dealership dealership = dealershipRepository.findById(request.getDealershipId())
                .orElseThrow(() -> new EntityNotFoundException("Dealership not found with ID: " + request.getDealershipId()));

        if (dealership.getStatus() == DealershipStatus.INACTIVE) {
            throw new IllegalStateException("Không thể tạo kho mới cho Đại lý đang ngừng hoạt động (INACTIVE).");
        }

        User currentUser = getCurrentUser();
        if (!currentUser.getRole().getRoleName().equalsIgnoreCase("ROLE_ADMIN")) {
            if (currentUser.getDealership() == null ||
                    !currentUser.getDealership().getDealershipId().equals(dealership.getDealershipId())) {
                throw new AccessDeniedException("Bạn chỉ được phép tạo kho cho Đại lý của mình.");
            }
        }

        Warehouse warehouse = mapper.toEntity(request);
        warehouse.setDealership(dealership);

        Warehouse saved = warehouseRepo.save(warehouse);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public WarehouseResponse getById(Long id) {
        Warehouse w = warehouseRepo.findHeaderById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        validateWarehouseAccess(w, getCurrentUser());

        var flats = stockRepo.findFlatByWarehouseId(id);

        var res = new WarehouseResponse();
        res.setWarehouseId(w.getWarehouseId());
        res.setWarehouseName(w.getWarehouseName());
        res.setWarehouseLocation(w.getWarehouseLocation());
        res.setDealershipId(w.getDealership().getDealershipId());
        res.setVehicleQuantity(flats.stream().mapToInt(WarehouseStockFlat::quantity).sum());
        res.setMaxCapacity(w.getMaxCapacity());

        res.setItems(flats.stream().map(f -> {
            var r = new WarehouseStockResponse();
            r.setModelCode(f.modelCode());
            r.setBrand(f.brand());
            r.setColor(f.color());
            r.setProductionYear(f.productionYear());
            r.setQuantity(f.quantity());

            var serials = vehicleSerialRepository
                    .findByModel_ModelIdAndWarehouse_WarehouseIdOrderBySeqNoAsc(
                            f.modelId(), w.getWarehouseId());

            List<VehicleSerialResponse> serialDetails = serials.stream()
                    .map(vs -> new VehicleSerialResponse(
                            vs.getVin(),
                            vs.getStatus(),
                            vs.getHoldUntil()
                    ))
                    .toList();
            r.setSerials(serialDetails);
            return r;
        }).toList());

        return res;
    }

    @Override
    public List<WarehouseResponse> getAll() {
        User user = getCurrentUser();
        List<Warehouse> headers;

        // [TỐI ƯU] Check user trước, sau đó mới gọi DB
        if (user.getRole().getRoleName().equalsIgnoreCase("ROLE_ADMIN")) {
            headers = warehouseRepo.findAllHeaders(); // Admin xem hết
        } else if (user.getDealership() != null) {
            // Staff chỉ xem kho của Dealer mình
            headers = warehouseRepo.findHeadersByDealershipId(user.getDealership().getDealershipId());
        } else {

            headers = new ArrayList<>();
        }

        return headers.stream().map(w -> {
            var flats = stockRepo.findFlatByWarehouseId(w.getWarehouseId());
            var res = new WarehouseResponse();
            res.setWarehouseId(w.getWarehouseId());
            res.setWarehouseName(w.getWarehouseName());
            res.setWarehouseLocation(w.getWarehouseLocation());
            res.setDealershipId(w.getDealership().getDealershipId());
            res.setVehicleQuantity(flats.stream().mapToInt(WarehouseStockFlat::quantity).sum());
            res.setMaxCapacity(w.getMaxCapacity());

            res.setItems(flats.stream().map(f -> {
                var r = new WarehouseStockResponse();
                r.setModelCode(f.modelCode());
                r.setBrand(f.brand());
                r.setColor(f.color());
                r.setProductionYear(f.productionYear());
                r.setQuantity(f.quantity());
                return r;
            }).toList());
            return res;
        }).toList();
    }

    @Override
    @Transactional
    public WarehouseResponse update(Long id, WarehouseRequest request) {
        Warehouse w = warehouseRepo.findHeaderById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        validateWarehouseAccess(w, getCurrentUser());

        mapper.updateEntity(w, request);
        warehouseRepo.save(w);

        return getById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Warehouse w = warehouseRepo.findHeaderById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        validateWarehouseAccess(w, getCurrentUser());

        warehouseRepo.delete(w);
    }

    @Override
    @Transactional
    public WarehouseResponse transferStock(Long sourceWarehouseId, Long targetWarehouseId,
                                           TransferStockRequest request) {
        // 1. Load Kho Nguồn, Kho Đích và Model
        Warehouse sourceWh = warehouseRepo.findHeaderById(sourceWarehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Kho nguồn (Source Warehouse) không tìm thấy"));

        Warehouse targetWh = warehouseRepo.findHeaderById(targetWarehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Kho đích (Target Warehouse) không tìm thấy"));

        Model model = modelRepository.findByModelCode(request.getModelCode())
                .orElseThrow(() -> new EntityNotFoundException("Model không tìm thấy: " + request.getModelCode()));

        // 2. Xác thực quyền truy cập
        validateWarehouseAccess(sourceWh, getCurrentUser());
        validateWarehouseAccess(targetWh, getCurrentUser());

        if (sourceWarehouseId.equals(targetWarehouseId)) {
            throw new IllegalArgumentException("Không thể chuyển hàng giữa cùng một kho.");
        }

        if (targetWh.getDealership().getStatus() == DealershipStatus.INACTIVE) {
            throw new IllegalStateException("Kho đích thuộc Đại lý đang ngừng hoạt động (INACTIVE), không thể nhận xe.");
        }

        // 3. Lấy Stock của Kho Nguồn và Kiểm tra số lượng
        WarehouseStock sourceStock = stockRepo.findByWarehouseAndModel(sourceWh, model)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Không tìm thấy stock của Model " + request.getModelCode() + " trong kho nguồn."
                ));

        int transferQty = request.getQuantity();
        int currentSourceQty = sourceStock.getQuantity();

        if (currentSourceQty < transferQty) {
            throw new IllegalArgumentException(
                    "Số lượng cần chuyển (" + transferQty + ") vượt quá số lượng hiện có (" + currentSourceQty + ") trong kho nguồn."
            );
        }

        // 4. Kiểm tra Capacity của Kho Đích
        final int WAREHOUSE_CAPACITY_LIMIT = targetWh.getMaxCapacity();
        int currentTargetTotal = stockRepo.sumQuantityByWarehouseId(targetWh.getWarehouseId());
        int projectedTargetTotal = currentTargetTotal + transferQty;

        if (projectedTargetTotal > WAREHOUSE_CAPACITY_LIMIT) {
            throw new IllegalArgumentException(
                    "Vượt quá sức chứa của kho đích. Giới hạn: " + WAREHOUSE_CAPACITY_LIMIT +
                            ". Dự kiến: " + projectedTargetTotal
            );
        }

        // 5. Cập nhật Stock Kho Nguồn (GIẢM)
        sourceStock.setQuantity(currentSourceQty - transferQty);
        stockRepo.save(sourceStock);

        // 6. Cập nhật Stock Kho Đích (TĂNG)
        WarehouseStock targetStock = stockRepo.findByWarehouseAndModel(targetWh, model)
                .orElseGet(() -> {
                    WarehouseStock s = new WarehouseStock();
                    s.setWarehouse(targetWh);
                    s.setModel(model);
                    s.setQuantity(0);
                    return s;
                });

        targetStock.setQuantity(targetStock.getQuantity() + transferQty);
        stockRepo.save(targetStock);

        Pageable pageable = PageRequest.of(0, transferQty); // Lấy N bản ghi đầu tiên (offset 0)
        List<VehicleSerial> serialsToTransfer = vehicleSerialRepository
                .findTopNSerialsForTransfer(sourceWh.getWarehouseId(), model.getModelId(), pageable);

        if (serialsToTransfer.size() < transferQty) {
            throw new IllegalStateException("Lỗi dữ liệu: Số lượng serial thực tế không đủ để chuyển.");
        }

        for (VehicleSerial vs : serialsToTransfer) {
            vs.setWarehouse(targetWh); // CHUYỂN KHO HÀNG
            // Không cần đổi status nếu vẫn AVAILABLE
        }
        vehicleSerialRepository.saveAll(serialsToTransfer);

        // 8. Cập nhật tổng số lượng xe của hai kho (VehicleQuantity)
        int newSourceTotal = stockRepo.sumQuantityByWarehouseId(sourceWh.getWarehouseId());
        sourceWh.setVehicleQuantity(newSourceTotal);
        warehouseRepo.save(sourceWh);

        targetWh.setVehicleQuantity(projectedTargetTotal);
        warehouseRepo.save(targetWh);

        // Trả về thông tin kho đích sau khi cập nhật
        return getById(targetWarehouseId);
    }

    @Override
    @Transactional
    public WarehouseResponse upsertStock(Long warehouseId, WarehouseStockRequest request) {
        Warehouse wh = warehouseRepo.findHeaderById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        validateWarehouseAccess(wh, getCurrentUser());

        Model model = modelRepository.findByModelCode(request.getModelCode())
                .orElseThrow(() -> new EntityNotFoundException("Model not found: " + request.getModelCode()));

        ElectricVehicle ev = vehicleRepo.findByModel_ModelCode(request.getModelCode())
                .orElseThrow(() -> new IllegalStateException("Chưa tạo xe đại diện cho model " + request.getModelCode()));

        if (wh.getDealership().getStatus() == DealershipStatus.INACTIVE) {
            throw new IllegalStateException("This dealership is INACTIVE, cannot update warehouse stock.");
        }

        WarehouseStock stock = stockRepo.findByWarehouseAndModel(wh, model)
                .orElseGet(() -> {
                    WarehouseStock s = new WarehouseStock();
                    s.setWarehouse(wh);
                    s.setModel(model);
                    s.setQuantity(0);
                    return s;
                });

        int oldQty = stock.getQuantity();
        int reqQty = request.getQuantity();
        int delta;
        int newQty;

        QtyMode mode = request.getMode() == null ? QtyMode.INCREMENT : request.getMode();
        switch (mode) {
            case SET -> {
                newQty = reqQty;
                delta = reqQty - oldQty;
            }
            case INCREMENT -> {
                if (reqQty <= 0) throw new IllegalArgumentException("quantity must be > 0 (INCREMENT)");
                newQty = oldQty + reqQty;
                delta = reqQty;
            }
            case DECREMENT -> {
                if (reqQty <= 0) throw new IllegalArgumentException("quantity phải > 0 (DECREMENT)");
                if (oldQty < reqQty) throw new IllegalArgumentException("can not decrement more than current stock");
                newQty = oldQty - reqQty;
                delta = -reqQty;
            }
            default -> throw new IllegalArgumentException("Unsupported QtyMode");
        }

        final int WAREHOUSE_CAPACITY_LIMIT = wh.getMaxCapacity();
        int currentTotal = stockRepo.sumQuantityByWarehouseId(wh.getWarehouseId());
        int projectedTotal = (currentTotal - oldQty) + newQty;

        if (projectedTotal > WAREHOUSE_CAPACITY_LIMIT) {
            throw new IllegalArgumentException(
                    "Warehouse capacity exceeded. Limit: " + WAREHOUSE_CAPACITY_LIMIT +
                            ". Projected: " + projectedTotal
            );
        }

        stock.setQuantity(newQty);
        stockRepo.save(stock);

        if (delta > 0) {
            int startSeq = vehicleSerialRepository.findMaxSeqNoByModel(model.getModelId());
            String colorLetter = vinGenerator.colorToLetter(model.getColor());
            int year = model.getProductionYear();
            Long vehicleId = ev.getVehicleId();
            long dealerShipId = wh.getDealership().getDealershipId();
            Long whId = wh.getWarehouseId();

            for (int i = 1; i <= delta; i++) {
                int seq = startSeq + i;
                String vin = vinGenerator.buildVin(year, dealerShipId, vehicleId, colorLetter, seq);
                VehicleSerial vs = new VehicleSerial();
                vs.setVehicle(ev);
                vs.setModel(model);
                vs.setWarehouse(wh);
                vs.setSeqNo(seq);
                vs.setColorCode(colorLetter);
                vs.setVin(vin);
                vs.setStatus(VehicleStatus.AVAILABLE);
                vehicleSerialRepository.save(vs);
            }
        } else if (delta < 0) {
            int needRemove = -delta;
            var lastSerials = vehicleSerialRepository
                    .findByModel_ModelIdAndWarehouse_WarehouseIdOrderBySeqNoDesc(
                            model.getModelId(), wh.getWarehouseId(), PageRequest.of(0, needRemove));
            vehicleSerialRepository.deleteAll(lastSerials);
        }

        wh.setVehicleQuantity(projectedTotal);
        warehouseRepo.save(wh);

        return getById(warehouseId);
    }

    @Override
    @Transactional
    public WarehouseResponse removeStock(Long warehouseId, String modelCode) {
        Warehouse w = warehouseRepo.findHeaderById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        validateWarehouseAccess(w, getCurrentUser());

        Model m = modelRepository.findByModelCode(modelCode)
                .orElseThrow(() -> new EntityNotFoundException("Model not found: " + modelCode));

        stockRepo.deleteByWarehouseAndModel(w, m);

        int total = stockRepo.sumQuantityByWarehouseId(w.getWarehouseId());
        w.setVehicleQuantity(total);
        warehouseRepo.save(w);

        return getById(warehouseId);
    }
}
