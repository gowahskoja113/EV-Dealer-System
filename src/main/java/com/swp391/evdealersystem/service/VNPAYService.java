package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.util.VnPayUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    @Value("${vnpay.ipnUrl}")     private String vnp_IpnUrl;

    public String createPaymentUrl(long amountVnd, String bankCode, long orderId, long paymentId, String purpose) {
        long vnpAmount = amountVnd * 100L;
        String vnp_TxnRef = String.valueOf(paymentId);
        String vnp_CreateDate = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        Map<String, String> vnp = new HashMap<>();
        vnp.put("vnp_Version", "2.1.0");
        vnp.put("vnp_Command", "pay");
        vnp.put("vnp_TmnCode", vnp_TmnCode);
        vnp.put("vnp_Amount", String.valueOf(vnpAmount));
        vnp.put("vnp_CurrCode", "VND");
        vnp.put("vnp_TxnRef", vnp_TxnRef);
        vnp.put("vnp_OrderInfo", "ORDER#" + orderId + "|PAYMENT#" + paymentId + "|PURPOSE=" + purpose);
        vnp.put("vnp_OrderType", "other");
        vnp.put("vnp_Locale", "vn");
        vnp.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp.put("vnp_IpAddr", "127.0.0.1");
        vnp.put("vnp_CreateDate", vnp_CreateDate);
        vnp.put("vnp_SecureHashType", "HmacSHA512");
        vnp.put("vnp_ExpireDate", ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
                .plusMinutes(15).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        if (bankCode != null && !bankCode.isBlank()) {
            vnp.put("vnp_BankCode", bankCode);
        }

        SortedMap<String, String> sorted = new TreeMap<>(vnp);
        StringBuilder query = new StringBuilder(), hashData = new StringBuilder();
        for (var e : sorted.entrySet()) {
            var k = e.getKey(); var v = e.getValue();
            if (v == null || v.isBlank()) continue;
            var ek = VnPayUtil.urlEncode(k);
            var ev = VnPayUtil.urlEncode(v);
            if (!"vnp_SecureHash".equals(k) && !"vnp_SecureHashType".equals(k)) {
                if (hashData.length() > 0) hashData.append("&");
                hashData.append(ek).append("=").append(ev);
            }
            if (query.length() > 0) query.append("&");
            query.append(ek).append("=").append(ev);
        }
        String vnp_SecureHash = VnPayUtil.hmacSHA512(vnp_HashSecret.trim(), hashData.toString());

        // debug
        System.out.println("==== VNPAY DEBUG ====");
        System.out.println("HashData: " + hashData);
        System.out.println("SecureHash: " + vnp_SecureHash);

        return vnp_Url + "?" + query + "&vnp_SecureHash=" + vnp_SecureHash;
    }

    /** Verify chữ ký VNPay gửi về */
    public boolean verifySignature(Map<String,String> params) {
        String receivedHash = params.get("vnp_SecureHash");
        Map<String,String> clone = new HashMap<>(params);
        clone.remove("vnp_SecureHash");
        clone.remove("vnp_SecureHashType");
        SortedMap<String,String> sorted = new TreeMap<>(clone);
        StringBuilder data = new StringBuilder();
        for (var e : sorted.entrySet()) {
            var v = e.getValue();
            if (v == null || v.isBlank()) continue;
            if (data.length() > 0) data.append("&");
            data.append(VnPayUtil.urlEncode(e.getKey()))
                    .append("=")
                    .append(VnPayUtil.urlEncode(v));
        }
        String calc = VnPayUtil.hmacSHA512(vnp_HashSecret.trim(), data.toString());
        return calc.equalsIgnoreCase(receivedHash);
    }
}
