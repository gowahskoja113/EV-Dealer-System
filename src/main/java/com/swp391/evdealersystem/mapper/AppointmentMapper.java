package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.AppointmentRequest;
import com.swp391.evdealersystem.entity.Appointment;
import com.swp391.evdealersystem.entity.Customer;
import com.swp391.evdealersystem.entity.User;
import com.swp391.evdealersystem.entity.ServiceEntity;
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

    public Appointment toEntity(AppointmentRequest req) {
        Appointment appointment = new Appointment();

        // Lấy reference cho các quan hệ ManyToOne
        Customer customer = customerRepository.getReferenceById(req.getCustomerId());
        User assignedUser = userRepository.getReferenceById(req.getAssignedUserId());
        ServiceEntity service = serviceRepository.getReferenceById(req.getServiceId());

        // Gán vào entity Appointment
        appointment.setCustomer(customer);
        appointment.setAssignedUser(assignedUser);
        appointment.setService(service);
        appointment.setStartAt(req.getStartAt());
        appointment.setEndAt(req.getEndAt());
        appointment.setNote(req.getNote());

        return appointment;
    }
}
