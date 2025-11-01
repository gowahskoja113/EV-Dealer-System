package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.WarehouseRequest;
import com.swp391.evdealersystem.dto.request.WarehouseStockRequest;
import com.swp391.evdealersystem.dto.response.WarehouseResponse;
import com.swp391.evdealersystem.dto.response.WarehouseStockFlat;
import com.swp391.evdealersystem.dto.response.WarehouseStockResponse;
import com.swp391.evdealersystem.entity.*;
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

    @Override
    @Transactional
    public WarehouseResponse create(WarehouseRequest request) {
        if (warehouseRepo.existsByWarehouseLocation(request.getWarehouseLocation())) {
            throw new IllegalArgumentException("Warehouse location already exists");
        }
        Warehouse warehouse = mapper.toEntity(request);
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
        res.setVehicleQuantity(flats.stream().mapToInt(WarehouseStockFlat::quantity).sum());
        res.setItems(flats.stream().map(f -> {
            var r = new WarehouseStockResponse();
            r.setModelCode(f.modelCode());
            r.setBrand(f.brand());
            r.setColor(f.color());
            r.setProductionYear(f.productionYear());
            r.setQuantity(f.quantity());
            // nếu cần VIN ở màn chi tiết:
            var serials = vehicleSerialRepository
                    .findByModel_ModelIdAndWarehouse_WarehouseIdOrderBySeqNoAsc(
                            f.modelId(), w.getWarehouseId());
            r.setVins(serials.stream().map(VehicleSerial::getVin).toList());
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
            res.setVehicleQuantity(flats.stream().mapToInt(WarehouseStockFlat::quantity).sum());
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

        // Tìm/khởi tạo stock (không cần chạm wh.getStocks())
        WarehouseStock stock = stockRepo.findByWarehouseAndModel(wh, model)
                .orElseGet(() -> {
                    WarehouseStock s = new WarehouseStock();
                    s.setWarehouse(wh);
                    s.setModel(model);
                    s.setQuantity(0);
                    return s;
                });

        int oldQty = stock.getQuantity();
        int newQty = request.getQuantity();
        int delta  = newQty - oldQty;

        // 1) cập nhật số lượng
        stock.setQuantity(newQty);
        stockRepo.save(stock);

        // 2) đồng bộ VIN theo delta
        if (delta > 0) {
            int startSeq = vehicleSerialRepository.findMaxSeqNoByModelAndWarehouse(
                    model.getModelId(), wh.getWarehouseId());

            String colorLetter = vinGenerator.colorToLetter(model.getColor());
            int year          = model.getProductionYear();
            Long vehicleId    = ev.getVehicleId();

            for (int i = 1; i <= delta; i++) {
                int seq   = startSeq + i;
                String vin = vinGenerator.buildVin(year, vehicleId, colorLetter, seq);

                VehicleSerial vs = new VehicleSerial();
                vs.setVehicle(ev);
                vs.setModel(model);
                vs.setWarehouse(wh);
                vs.setSeqNo(seq);
                vs.setColorCode(colorLetter);
                vs.setVin(vin);
                vehicleSerialRepository.save(vs);
            }
        } else if (delta < 0) {
            int needRemove = -delta;
            var lastSerials = vehicleSerialRepository
                    .findByModel_ModelIdAndWarehouse_WarehouseIdOrderBySeqNoDesc(
                            model.getModelId(), wh.getWarehouseId(), PageRequest.of(0, needRemove));
            vehicleSerialRepository.deleteAll(lastSerials);
        }

        // 3) tổng số trong kho (không dựa vào wh.getStocks)
        int total = stockRepo.sumQuantityByWarehouseId(wh.getWarehouseId());
        wh.setVehicleQuantity(total);
        warehouseRepo.save(wh);

        // 4) trả response bằng projection + gắn VIN theo modelId
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
