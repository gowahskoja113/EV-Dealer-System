package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.CustomerRequest;
import com.swp391.evdealersystem.dto.response.CustomerResponse;
import com.swp391.evdealersystem.entity.Customer;
import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.mapper.CustomerMapper;
import com.swp391.evdealersystem.repository.CustomerRepository;
import com.swp391.evdealersystem.repository.ElectricVehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ElectricVehicleRepository vehicleRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        ElectricVehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + request.getVehicleId()));

        if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists: " + request.getPhoneNumber());
        }

        Customer customer = customerMapper.toEntity(request, vehicle);
        Customer saved = customerRepository.save(customer);
        return customerMapper.toResponse(saved);
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
        return customerMapper.toResponse(updated);
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
    }

    @Override
    public CustomerResponse getCustomerByPhone(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber)
                .map(customerMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Customer not found with phone: " + phoneNumber));
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with ID: " + id);
        }
        customerRepository.deleteById(id);
    }
}
