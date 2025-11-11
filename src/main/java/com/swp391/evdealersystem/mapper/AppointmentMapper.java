package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.CreateAppointmentRequest;
import com.swp391.evdealersystem.dto.response.AppointmentResponse;
import com.swp391.evdealersystem.entity.Appointment;
import com.swp391.evdealersystem.entity.Customer;
import com.swp391.evdealersystem.entity.User;
import com.swp391.evdealersystem.entity.ServiceEntity;
import com.swp391.evdealersystem.enums.AppointmentStatus;
import com.swp391.evdealersystem.repository.CustomerRepository;
import com.swp391.evdealersystem.repository.UserRepository;
import com.swp391.evdealersystem.repository.ServiceRepository;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    private final ServiceRepository serviceRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public AppointmentMapper(ServiceRepository serviceRepository,
                             CustomerRepository customerRepository,
                             UserRepository userRepository) {
        this.serviceRepository = serviceRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    public Appointment toEntity(CreateAppointmentRequest r) {
        Appointment a = new Appointment();

        // Lấy reference cho các quan hệ ManyToOne
        Customer customerRef = customerRepository.getReferenceById(r.getCustomerId());
        User assignedUserRef = userRepository.getReferenceById(r.getAssignedUserId());
        ServiceEntity serviceRef = serviceRepository.getReferenceById(r.getServiceId());

        // Gán vào entity (đúng kiểu thay vì set ID thô)
        a.setCustomer(customerRef);
        a.setAssignedUser(assignedUserRef);
        a.setService(serviceRef);

        // Các trường primitive/embedded khác
        a.setWarehouseId(r.getWarehouseId());
        a.setStartAt(r.getStartAt());
        a.setEndAt(r.getEndAt());
        a.setStatus(AppointmentStatus.SCHEDULED);
        a.setNote(r.getNote());

        return a;
    }

    public AppointmentResponse toResponse(Appointment a) {
        return new AppointmentResponse(a.getAppointmentId(), a.getStatus());
    }
}
