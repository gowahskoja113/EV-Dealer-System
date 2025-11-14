package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.CustomerRequest;
import com.swp391.evdealersystem.dto.response.CustomerResponse;
import com.swp391.evdealersystem.dto.response.CustomerWithOrdersResponse;

import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse updateCustomer(Long id, CustomerRequest request);

    CustomerResponse getCustomerById(Long id);

    CustomerResponse getCustomerByPhone(String phoneNumber);

    List<CustomerResponse> getAllCustomers();

    void deleteCustomer(Long id);

    CustomerResponse assignSales(Long customerId, Long userId);

    CustomerResponse unassignSales(Long customerId);

    CustomerWithOrdersResponse getCustomerWithOrdersById(Long customerId);
}

