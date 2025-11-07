package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.WarehouseRequest;
import com.swp391.evdealersystem.dto.request.WarehouseStockRequest;
import com.swp391.evdealersystem.dto.response.VehicleSerialResponse;
import com.swp391.evdealersystem.dto.response.WarehouseResponse;
import com.swp391.evdealersystem.dto.response.WarehouseStockFlat;
import com.swp391.evdealersystem.dto.response.WarehouseStockResponse;
import com.swp391.evdealersystem.entity.*;
import com.swp391.evdealersystem.enums.QtyMode;
import com.swp391.evdealersystem.enums.VehicleStatus;
import com.swp391.evdealersystem.mapper.WarehouseMapper;
import com.swp391.evdealersystem.repository.*;
import com.swp391.evdealersystem.util.VinGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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

    @Override
    @Transactional
    public WarehouseResponse create(WarehouseRequest request) {
        if (warehouseRepo.existsByWarehouseLocation(request.getWarehouseLocation())) {
            throw new IllegalArgumentException("Warehouse location already exists");
        }
        // 1. Tìm Dealership
        Dealership dealership = dealershipRepository.findById(request.getDealershipId())
                .orElseThrow(() -> new EntityNotFoundException("Dealership not found with ID: " + request.getDealershipId()));

        // 2. Chuyển đổi DTO sang Entity (như cũ)
        Warehouse warehouse = mapper.toEntity(request);

        // 3. Gán quan hệ
        warehouse.setDealership(dealership); // <-- Đây là bước quan trọng nhất

        // 4. Lưu và trả về (như cũ)
        Warehouse saved = warehouseRepo.save(warehouse);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public WarehouseResponse getById(Long id) {
        Warehouse w = warehouseRepo.findHeaderById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

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
        List<Warehouse> headers = warehouseRepo.findAllHeaders();
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
        mapper.updateEntity(w, request);
        warehouseRepo.save(w);

        return getById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Warehouse w = warehouseRepo.findHeaderById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));
        warehouseRepo.delete(w);
    }

    @Override
    @Transactional
    public WarehouseResponse upsertStock(Long warehouseId, WarehouseStockRequest request) {
        Warehouse wh = warehouseRepo.findHeaderById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        Model model = modelRepository.findByModelCode(request.getModelCode())
                .orElseThrow(() -> new EntityNotFoundException("Model not found: " + request.getModelCode()));

        ElectricVehicle ev = vehicleRepo.findByModel_ModelCode(request.getModelCode())
                .orElseThrow(() -> new IllegalStateException("Chưa tạo xe đại diện cho model " + request.getModelCode()));

        // lấy/khởi tạo stock
        WarehouseStock stock = stockRepo.findByWarehouseAndModel(wh, model)
                .orElseGet(() -> {
                    WarehouseStock s = new WarehouseStock();
                    s.setWarehouse(wh);
                    s.setModel(model);
                    s.setQuantity(0);
                    return s;
                });

        int oldQty = stock.getQuantity(); // Số lượng cũ của model này
        int reqQty = request.getQuantity();
        int delta;
        int newQty; // Số lượng mới của model này

        // ---- TÍNH NEW QTY THEO MODE ----
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

        // === START: KIỂM TRA GIỚI HẠN 20 XE ===
        final int WAREHOUSE_CAPACITY_LIMIT = wh.getMaxCapacity();

        // 1. Lấy tổng số lượng hiện tại của TẤT CẢ các model trong kho
        int currentTotal = stockRepo.sumQuantityByWarehouseId(wh.getWarehouseId());

        // 2. Tính tổng dự kiến
        // (tổng hiện tại - số lượng cũ của model này + số lượng mới của model này)
        int projectedTotal = (currentTotal - oldQty) + newQty;

        if (projectedTotal > WAREHOUSE_CAPACITY_LIMIT) {
            throw new IllegalArgumentException(
                    "Warehouse capacity exceeded. Limit for this warehouse is " + WAREHOUSE_CAPACITY_LIMIT +
                            ". Current (other models): " + (currentTotal - oldQty) +
                            ", Trying to set this model to: " + newQty +
                            ", Projected Total: " + projectedTotal
            );
        }

        // Cập nhật số lượng cho stock này
        stock.setQuantity(newQty);
        stockRepo.save(stock);

        // ---- ĐỒNG BỘ VIN THEO DELTA ----
        if (delta > 0) {
            // ... (Logic tạo VIN của bạn giữ nguyên) ...
            int startSeq = vehicleSerialRepository.findMaxSeqNoByModelAndWarehouse(
                    model.getModelId(), wh.getWarehouseId());
            String colorLetter = vinGenerator.colorToLetter(model.getColor());
            int year = model.getProductionYear();
            Long vehicleId = ev.getVehicleId();

            for (int i = 1; i <= delta; i++) {
                int seq = startSeq + i;
                String vin = vinGenerator.buildVin(year, vehicleId, colorLetter, seq);
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
            // ... (Logic xóa VIN của bạn giữ nguyên) ...
            int needRemove = -delta;
            var lastSerials = vehicleSerialRepository
                    .findByModel_ModelIdAndWarehouse_WarehouseIdOrderBySeqNoDesc(
                            model.getModelId(), wh.getWarehouseId(), PageRequest.of(0, needRemove));
            vehicleSerialRepository.deleteAll(lastSerials);
        }

        // Cập nhật tổng số lượng của Warehouse (dùng luôn số đã tính cho tối ưu)
        wh.setVehicleQuantity(projectedTotal);
        warehouseRepo.save(wh);

        return getById(warehouseId);
    }


    @Override
    @Transactional
    public WarehouseResponse removeStock(Long warehouseId, String modelCode) {
        Warehouse w = warehouseRepo.findHeaderById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        Model m = modelRepository.findByModelCode(modelCode)
                .orElseThrow(() -> new EntityNotFoundException("Model not found: " + modelCode));

        stockRepo.deleteByWarehouseAndModel(w, m);

        int total = stockRepo.sumQuantityByWarehouseId(w.getWarehouseId());
        w.setVehicleQuantity(total);
        warehouseRepo.save(w);

        return getById(warehouseId);
    }
}
