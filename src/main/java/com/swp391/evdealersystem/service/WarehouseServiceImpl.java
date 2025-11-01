package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.WarehouseRequest;
import com.swp391.evdealersystem.dto.request.WarehouseStockRequest;
import com.swp391.evdealersystem.dto.response.WarehouseResponse;
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
        Warehouse w = warehouseRepo.findWithStocksById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));
        // Không còn EV theo warehouse, trả thẳng mapper
        return mapper.toResponse(w);
    }

    @Override
    public List<WarehouseResponse> getAll() {
        // Lấy toàn bộ kho + stocks, không đụng tới EV
        return warehouseRepo.findAllWithStocks().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public WarehouseResponse update(Long id, WarehouseRequest request) {
        Warehouse w = warehouseRepo.findWithStocksById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));
        mapper.updateEntity(w, request);
        Warehouse saved = warehouseRepo.save(w);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Warehouse w = warehouseRepo.findWithStocksById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));
        warehouseRepo.delete(w);
    }

    @Override
    @Transactional
    public WarehouseResponse upsertStock(Long warehouseId, WarehouseStockRequest request) {
        Warehouse wh = warehouseRepo.findWithStocksById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        Model model = modelRepository.findByModelCode(request.getModelCode())
                .orElseThrow(() -> new EntityNotFoundException("Model not found: " + request.getModelCode()));

        // EV đại diện (bắt buộc tồn tại)
        ElectricVehicle ev = vehicleRepo.findByModel_ModelCode(request.getModelCode())
                .orElseThrow(() -> new IllegalStateException("Chưa tạo xe đại diện cho model " + request.getModelCode()));

        // Tìm/khởi tạo stock
        WarehouseStock stock = stockRepo.findByWarehouseAndModel(wh, model)
                .orElseGet(() -> {
                    WarehouseStock s = new WarehouseStock();
                    s.setWarehouse(wh);
                    s.setModel(model);
                    s.setQuantity(0);
                    wh.getStocks().add(s);
                    return s;
                });

        int oldQty = stock.getQuantity();
        int newQty = request.getQuantity();
        int delta  = newQty - oldQty;

        // 1) Cập nhật số lượng
        stock.setQuantity(newQty);
        stockRepo.save(stock);

        // 2) Đồng bộ VIN theo delta
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

        // 3) Tổng số trong kho
        int total = wh.getStocks().stream().mapToInt(WarehouseStock::getQuantity).sum();
        wh.setVehicleQuantity(total);
        warehouseRepo.save(wh);

        // 4) Trả response + gắn danh sách VIN cho từng item
        WarehouseResponse res = mapper.toResponse(wh);
        res.getItems().forEach(item -> {
            Long modelId = wh.getStocks().stream()
                    .filter(s -> s.getModel().getModelCode().equals(item.getModelCode()))
                    .map(s -> s.getModel().getModelId())
                    .findFirst().orElse(null);

            if (modelId != null) {
                var serials = vehicleSerialRepository
                        .findByModel_ModelIdAndWarehouse_WarehouseIdOrderBySeqNoAsc(modelId, wh.getWarehouseId());
                item.setVins(serials.stream().map(VehicleSerial::getVin).toList());
            } else {
                item.setVins(java.util.Collections.emptyList());
            }
        });
        return res;
    }


    @Override
    @Transactional
    public WarehouseResponse removeStock(Long warehouseId, String modelCode) {
        Warehouse w = warehouseRepo.findWithStocksById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        Model m = modelRepository.findByModelCode(modelCode)
                .orElseThrow(() -> new EntityNotFoundException("Model not found: " + modelCode));

        stockRepo.deleteByWarehouseAndModel(w, m);
        w.getStocks().removeIf(s -> s.getModel().getModelId().equals(m.getModelId()));

        int total = w.getStocks().stream().mapToInt(WarehouseStock::getQuantity).sum();
        w.setVehicleQuantity(total);
        warehouseRepo.save(w);

        return mapper.toResponse(w);
    }
}
