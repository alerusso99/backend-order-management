package com.alessandro.backend.order_management.mapper;

import com.alessandro.backend.order_management.dto.UserResponse;
import com.alessandro.backend.order_management.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }
}
