package com.alessandro.backend.order_management.dto;

public class LoginResponse {

    private final String accessToken;
    private final String tokenType = "Bearer";

    public LoginResponse(String accessToken) { this.accessToken = accessToken; }

    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }

}
