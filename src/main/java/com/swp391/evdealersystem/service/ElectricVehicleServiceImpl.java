package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.ElectricVehicleRequest;
import com.swp391.evdealersystem.dto.response.ElectricVehicleResponse;
import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.Model;
import com.swp391.evdealersystem.enums.VehicleStatus;
import com.swp391.evdealersystem.mapper.ElectricVehicleMapper;
import com.swp391.evdealersystem.repository.ElectricVehicleRepository;
import com.swp391.evdealersystem.repository.ModelRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ElectricVehicleServiceImpl implements ElectricVehicleService {

    private final ElectricVehicleRepository evRepo;
    private final ModelRepository modelRepo;
    private final ElectricVehicleMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ElectricVehicleResponse> getAll() {
        return evRepo.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ElectricVehicleResponse> getByModelCode(String modelCode) {
        Model model = modelRepo.findByModelCode(modelCode)
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + modelCode));
        return evRepo.findByModel(model).stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public ElectricVehicleResponse create(ElectricVehicleRequest req) {
        Model model = (req.getModelCode() != null)
                ? modelRepo.findByModelCode(req.getModelCode())
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + req.getModelCode()))
                : modelRepo.findById(req.getModelId())
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + req.getModelId()));

        // enforce “1 model ↔ 1 EV”
        if (evRepo.existsByModel_ModelCode(model.getModelCode())) {
            throw new IllegalArgumentException("Đã tồn tại xe đại diện cho modelCode=" + model.getModelCode());
        }

        ElectricVehicle ev = mapper.toEntity(req, model);
        ev = evRepo.save(ev);
        return mapper.toResponse(ev);
    }

    @Override
    @Transactional(readOnly = true)
    public ElectricVehicleResponse getById(Long vehicleId) {
        ElectricVehicle ev = evRepo.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId));
        return mapper.toResponse(ev);
    }

    @Override
    @Transactional
    public ElectricVehicleResponse update(Long vehicleId, ElectricVehicleRequest req) {
        ElectricVehicle ev = evRepo.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId));

        Model model = null;
        if (req.getModelCode() != null) {
            model = modelRepo.findByModelCode(req.getModelCode())
                    .orElseThrow(() -> new IllegalArgumentException("Model not found: " + req.getModelCode()));
        } else if (req.getModelId() != null) {
            model = modelRepo.findById(req.getModelId())
                    .orElseThrow(() -> new IllegalArgumentException("Model not found: " + req.getModelId()));
        }

        mapper.updateEntity(ev, req, model);
        ev = evRepo.save(ev);
        return mapper.toResponse(ev);
    }

    @Override
    @Transactional
    public void delete(Long vehicleId) {
        if (!evRepo.existsById(vehicleId)) {
            throw new IllegalArgumentException("Vehicle not found: " + vehicleId);
        }
        evRepo.deleteById(vehicleId);
    }

    @Transactional
    @Override
    public void importVehicleTypeExcel(MultipartFile file) throws Exception {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        int saved = 0, skipped = 0;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String modelCode = getString(row.getCell(0));
            if (modelCode == null || modelCode.isBlank()) {
                skipped++;
                continue;
            }
            modelCode = modelCode.trim();

            // ======= 1) UPSERT MODEL =======
            Model model = modelRepo.findByModelCode(modelCode)
                    .orElseGet(Model::new);

            model.setModelCode(modelCode);
            model.setBrand(getString(row.getCell(1)));
            model.setColor(getString(row.getCell(2)));
            model.setProductionYear(getInteger(row.getCell(3)));

            model = modelRepo.save(model);

            // ======= 2) UPSERT ELECTRIC_VEHICLE =======
            ElectricVehicle ev = evRepo.findByModel_ModelCode(modelCode)
                    .orElseGet(ElectricVehicle::new);

            ev.setModel(model);
            ev.setCost(getBigDecimal(row.getCell(4)));
            ev.setPrice(getBigDecimal(row.getCell(5)));
            ev.setBatteryCapacity(getInteger(row.getCell(6)));
            ev.setImageUrl(getString(row.getCell(7)));

            String statusStr = getString(row.getCell(8));
            if (statusStr != null && !statusStr.isBlank()) {
                ev.setStatus(VehicleStatus.valueOf(statusStr.trim().toUpperCase()));
            } else if (ev.getStatus() == null) {
                ev.setStatus(VehicleStatus.AVAILABLE);
            }

            evRepo.save(ev);
            saved++;
        }

        workbook.close();
        System.out.println("IMPORT VEHICLE_TYPE DONE saved=" + saved + " skipped=" + skipped);
    }

    // ===== helper parse =====
    private String getString(Cell cell) {
        if (cell == null) return null;
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    private Integer getInteger(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }
        String s = getString(cell);
        return (s == null || s.isBlank()) ? null : Integer.parseInt(s);
    }

    private BigDecimal getBigDecimal(Cell cell) {
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }

        String s = getString(cell);
        if (s == null || s.isBlank()) return null;

        s = s.replace(",", "");

        return new BigDecimal(s);
    }

}

