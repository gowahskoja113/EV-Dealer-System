package com.swp391.evdealersystem.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VnpIpnResponse {

    @JsonProperty("RspCode")
    private String rspCode;

    @JsonProperty("Message")
    private String message;

    private boolean success;

    public VnpIpnResponse() {}

    public VnpIpnResponse(String rspCode, String message, boolean success) {
        this.rspCode = rspCode;
        this.message = message;
        this.success = success;
    }

    public static VnpIpnResponse ok(String code, String msg) {
        return new VnpIpnResponse(code, msg, true);
    }
    public static VnpIpnResponse fail(String code, String msg) {
        return new VnpIpnResponse(code, msg, false);
    }

    public String getRspCode() { return rspCode; }
    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }
}