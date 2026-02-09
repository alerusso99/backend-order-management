package com.alessandro.backend.order_management.exception;


public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(Long id)
    {
        super("User not found "+id);
    }
}
