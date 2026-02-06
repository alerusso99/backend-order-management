package com.alessandro.backend.order_management.controller;

import com.alessandro.backend.order_management.dto.CreateUserRequest;
import com.alessandro.backend.order_management.dto.UserResponse;
import com.alessandro.backend.order_management.entity.User;
import com.alessandro.backend.order_management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody CreateUserRequest request) {

        User created = userService.create(request.getEmail(), request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id){
        User user = userService.getById(id);
        return ResponseEntity.ok(toResponse(user));
    }

    private UserResponse toResponse(User user){
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail()
            );
    }


}
