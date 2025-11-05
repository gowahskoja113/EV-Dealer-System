package com.swp391.evdealersystem.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VNPAYService {

    @Value("${vnpay.tmnCode}")    private String vnp_TmnCode;
    @Value("${vnpay.hashSecret}") private String vnp_HashSecret;
    @Value("${vnpay.url}")        private String vnp_Url;
    @Value("${vnpay.returnUrl}")  private String vnp_ReturnUrl;

    public String createPaymentUrl(long amount, String bankCode, String orderInfo) {
        long vnpAmount = amount * 100L;

        String vnp_TxnRef = String.valueOf(System.currentTimeMillis());
        String vnp_IpAddr = "127.0.0.1";
        String vnp_CreateDate = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        Map<String, String> vnp = new HashMap<>();
        vnp.put("vnp_Version", "2.1.0");
        vnp.put("vnp_Command", "pay");
        vnp.put("vnp_TmnCode", vnp_TmnCode);
        vnp.put("vnp_Amount", String.valueOf(vnpAmount));
        vnp.put("vnp_CurrCode", "VND");
        vnp.put("vnp_TxnRef", vnp_TxnRef);
        vnp.put("vnp_OrderInfo", orderInfo);
        vnp.put("vnp_OrderType", "other");
        vnp.put("vnp_Locale", "vn");
        vnp.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp.put("vnp_IpAddr", vnp_IpAddr);
        vnp.put("vnp_CreateDate", vnp_CreateDate);
        vnp.put("vnp_SecureHashType", "HmacSHA512");

        if (bankCode != null && !bankCode.isBlank()) {
            vnp.put("vnp_BankCode", bankCode);
        }

        // Sort theo tên key
        List<String> names = new ArrayList<>(vnp.keySet());
        Collections.sort(names);

        // Build cặp key=value đã URL-encode
        List<String> hashPairs = new ArrayList<>();
        List<String> queryPairs = new ArrayList<>();
        for (String name : names) {
            String value = vnp.get(name);
            if (value == null || value.isBlank()) continue;

            String enName  = URLEncoder.encode(name, StandardCharsets.US_ASCII);
            String enValue = URLEncoder.encode(value, StandardCharsets.US_ASCII);

            // KHÔNG đưa 2 tham số này vào chuỗi ký
            if (!name.equals("vnp_SecureHash") && !name.equals("vnp_SecureHashType")) {
                hashPairs.add(enName + "=" + enValue);
            }
            // Query vẫn giữ nguyên đầy đủ (có vnp_SecureHashType)
            queryPairs.add(enName + "=" + enValue);
        }

        String hashData = String.join("&", hashPairs); // chuỗi đem ký
        String query    = String.join("&", queryPairs); // phần nền URL

        String vnp_SecureHash = hmacSHA512(vnp_HashSecret.trim(), hashData);
        String paymentUrl = vnp_Url + "?" + query + "&vnp_SecureHash=" + vnp_SecureHash;

        // Debug
        System.out.println("==== VNPAY DEBUG ====");
        System.out.println("HashData: " + hashData);
        System.out.println("SecureHash: " + vnp_SecureHash);
        System.out.println("URL: " + paymentUrl);

        return paymentUrl;
    }

    private String hmacSHA512(String key, String data) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA512");
            mac.init(new javax.crypto.spec.SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(raw.length * 2);
            for (byte b : raw) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("HmacSHA512 error", e);
        }
    }
}
