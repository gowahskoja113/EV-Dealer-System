package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.CustomerRequest;
import com.swp391.evdealersystem.dto.response.CustomerResponse;
import com.swp391.evdealersystem.entity.Customer;
import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.repository.CustomerRepository;
import com.swp391.evdealersystem.repository.ElectricVehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ElectricVehicleRepository vehicleRepository;

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        ElectricVehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + request.getVehicleId()));

        if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists: " + request.getPhoneNumber());
        }

        Customer customer = new Customer();
        customer.setVehicle(vehicle);
        customer.setName(request.getName());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setInterestVehicle(request.getInterestVehicle());
        customer.setStatus(request.getStatus());

        Customer saved = customerRepository.save(customer);
        return mapToResponse(saved);
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));

        ElectricVehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + request.getVehicleId()));

        existing.setVehicle(vehicle);
        existing.setName(request.getName());
        existing.setPhoneNumber(request.getPhoneNumber());
        existing.setInterestVehicle(request.getInterestVehicle());
        existing.setStatus(request.getStatus());

        Customer updated = customerRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
        return mapToResponse(customer);
    }

    @Override
    public CustomerResponse getCustomerByPhone(String phoneNumber) {
        Customer customer = customerRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Customer not found with phone: " + phoneNumber));
        return mapToResponse(customer);
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with ID: " + id);
        }
        customerRepository.deleteById(id);
    }

    // -----------------------
    // Helper mapper
    // -----------------------
    private CustomerResponse mapToResponse(Customer entity) {
        return CustomerResponse.builder()
                .customerId(entity.getCustomerId())
                .vehicleId(entity.getVehicle().getVehicleId())
                .vehicleModel(entity.getVehicle().getModel())
                .name(entity.getName())
                .phoneNumber(entity.getPhoneNumber())
                .interestVehicle(entity.getInterestVehicle())
                .status(entity.getStatus())
                .build();
    }
}
