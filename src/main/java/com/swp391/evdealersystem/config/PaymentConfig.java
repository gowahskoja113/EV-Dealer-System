package com.swp391.evdealersystem.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "vnpay")
public class PaymentConfig {
    private String tmnCode;
    private String hashSecret;
    private String payUrl; // https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
    private String returnUrl; // https://your-domain.com/api/payments/vnpay/return
    private String ipnUrl; // https://your-domain.com/api/payments/vnpay/ipn
    private String version = "2.1.0";
    private String command = "pay";
    private String locale = "vn";
    private String currCode = "VND";
}