package com.swp391.evdealersystem.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class VnPayUtil {
    private VnPayUtil() {}

    public static String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(secretKey);
            byte[] hash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) sb.append(String.format("%02x", b & 0xff));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error while generating HMAC SHA512", e);
        }
    }

    /** sort params & build query, then return [query, secureHash] */
    public static Map.Entry<String,String> buildSignedQuery(Map<String, String> params, String secret) {
        SortedMap<String, String> sorted = new TreeMap<>(params);
        StringBuilder query = new StringBuilder();
        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String,String> e : sorted.entrySet()) {
            if (e.getValue() == null || e.getValue().isEmpty()) continue;
            String encKey = urlEncode(e.getKey());
            String encVal = urlEncode(e.getValue());
            query.append(encKey).append("=").append(encVal).append("&");
            hashData.append(encKey).append("=").append(encVal).append("&");
        }
        if (query.length() > 0) query.setLength(query.length() - 1);
        if (hashData.length() > 0) hashData.setLength(hashData.length() - 1);
        String secureHash = hmacSHA512(secret, hashData.toString());
        return new AbstractMap.SimpleEntry<>(query.toString(), secureHash);
    }

    public static String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
