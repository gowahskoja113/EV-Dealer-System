package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.AppointmentRequest;
import com.swp391.evdealersystem.dto.response.AppointmentResponse;
import com.swp391.evdealersystem.entity.Appointment;
import com.swp391.evdealersystem.repository.CustomerRepository;
import com.swp391.evdealersystem.repository.SlotRepository;
import com.swp391.evdealersystem.repository.UserRepository;
import com.swp391.evdealersystem.repository.ServiceRepository;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    private final ServiceRepository serviceRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final SlotRepository slotRepository;

    public AppointmentMapper(ServiceRepository serviceRepository,
                             CustomerRepository customerRepository,
                             UserRepository userRepository,
                             SlotRepository slotRepository) {
        this.serviceRepository = serviceRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.slotRepository = slotRepository;
    }

    public Appointment toEntity(AppointmentRequest req) {
        Appointment appointment = new Appointment();

        appointment.setCustomer(customerRepository.getReferenceById(req.getCustomerId()));
        appointment.setAssignedUser(userRepository.getReferenceById(req.getAssignedUserId()));
        appointment.setService(serviceRepository.getReferenceById(req.getServiceId()));
        appointment.setNote(req.getNote());

        return appointment;
    }


        public AppointmentResponse toResponse(Appointment a) {
            if (a == null) return null;

            AppointmentResponse res = new AppointmentResponse();
            res.setAppointmentId(a.getAppointmentId());
            res.setStatus(a.getStatus());
            return res;
        }
    }


