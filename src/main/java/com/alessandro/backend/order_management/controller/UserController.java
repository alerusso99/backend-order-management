package com.alessandro.backend.order_management.controller;

import com.alessandro.backend.order_management.dto.CreateUserRequest;
import com.alessandro.backend.order_management.dto.PagedResponse;
import com.alessandro.backend.order_management.dto.UserResponse;
import com.alessandro.backend.order_management.entity.User;
import com.alessandro.backend.order_management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {

        User created = userService.create(request.getEmail(), request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<UserResponse>> list(Pageable pageable) {
        Page<User> page = userService.list(pageable);
        List<UserResponse> items = page.getContent().stream().map(this::toResponse).toList();
        PagedResponse<UserResponse> body = new PagedResponse<>(items, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.hasNext(), page.hasPrevious());
        return ResponseEntity.ok(body);
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
