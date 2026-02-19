package com.alessandro.backend.order_management.dto;

public class UserResponse {

    private Long id;
    private String name;
    private String email;

    public UserResponse(Long id, String email, String name) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
