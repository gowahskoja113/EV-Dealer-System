package com.swp391.evdealersystem.dto.response;


import com.swp391.evdealersystem.entity.Customer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class CustomerWithOrdersResponse {
    private Long customerId;
    private String name;
    private String phoneNumber;
    private String address;
    private List<OrderResponse> orders;  // Danh sách các đơn hàng của khách hàng

    // Constructor
    public CustomerWithOrdersResponse(Customer customer, List<OrderResponse> orders) {
        this.customerId = customer.getCustomerId();
        this.name = customer.getName();
        this.phoneNumber = customer.getPhoneNumber();
        this.address = customer.getAddress();
        this.orders = orders;
    }

}
