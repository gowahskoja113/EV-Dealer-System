package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.ServiceRecordRequest;
import com.swp391.evdealersystem.dto.request.ServiceRecordUpdateRequest;
import com.swp391.evdealersystem.dto.response.ServiceRecordResponse;
import com.swp391.evdealersystem.entity.ServiceRecord;
import com.swp391.evdealersystem.enums.AppointmentStatus;
import com.swp391.evdealersystem.mapper.ServiceRecordMapper;
import com.swp391.evdealersystem.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceRecordServiceImpl implements ServiceRecordService {

    private final ServiceRecordRepository repo;
    private final AppointmentRepository appointmentRepo;
    private final ServiceRecordMapper mapper;

    @Override
    public ServiceRecordResponse create(ServiceRecordRequest req) {

        // 1. Lấy Appointment
        var appointment = appointmentRepo.findById(req.getAppointmentId())
                .orElseThrow(() -> nf("Appointment", req.getAppointmentId()));

        // 2. Kiểm tra appointment đã có record chưa (dùng repo)
        if (repo.existsByAppointment_AppointmentId(req.getAppointmentId())) {
            throw new RuntimeException("Appointment already has a service record");
        }

        // 3. Tạo entity
        ServiceRecord e = new ServiceRecord();
        e.setAppointment(appointment);        // rất quan trọng
        e.setContent(req.getContent());
        e.setNote(req.getNote());

        // PrePersist sẽ tự sync user, customer, service
        ServiceRecord saved = repo.save(e);

        // 4. (tuỳ bạn) cập nhật trạng thái cuộc hẹn
        // appointment.setStatus(AppointmentStatus.COMPLETED);
        // appointmentRepo.save(appointment);

        return mapper.toResponse(saved);
    }


    @Override
    public ServiceRecordResponse get(Long id){
        return mapper.toResponse(repo.findById(id).orElseThrow(() -> nf("ServiceRecord", id)));
    }

    @Override
    public Page<ServiceRecordResponse> list(Pageable p){
        return repo.findAll(p).map(mapper::toResponse);
    }

    @Override
    public Page<ServiceRecordResponse> byCustomer(Long customerId, Pageable p){
        return repo.findByCustomer_CustomerId(customerId, p).map(mapper::toResponse);
    }

    @Override
    public Page<ServiceRecordResponse> byServiceEntity(Long serviceId, Pageable p){
        return repo.findByService_Id(serviceId, p).map(mapper::toResponse);
    }

    @Override
    public Page<ServiceRecordResponse> byUser(Long userId, Pageable p){
        return repo.findByUser_UserId(userId, p).map(mapper::toResponse);
    }

    @Override
    public ServiceRecordResponse update(Long id, ServiceRecordUpdateRequest req){
        ServiceRecord e = repo.findById(id).orElseThrow(() -> nf("ServiceRecord", id));
        if (req.getContent() != null) e.setContent(req.getContent());
        if (req.getNote() != null) e.setNote(req.getNote());
        return mapper.toResponse(repo.save(e));
    }

    @Override
    public void delete(Long id){
        repo.deleteById(id);
    }

    private EntityNotFoundException nf(String type, Object id){
        return new EntityNotFoundException(type + " not found: " + id);
    }
}
