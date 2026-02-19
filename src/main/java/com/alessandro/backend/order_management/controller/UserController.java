package com.alessandro.backend.order_management.controller;

import com.alessandro.backend.order_management.dto.CreateUserRequest;
import com.alessandro.backend.order_management.dto.PagedResponse;
import com.alessandro.backend.order_management.dto.UserResponse;
import com.alessandro.backend.order_management.entity.User;
import com.alessandro.backend.order_management.mapper.UserMapper;
import com.alessandro.backend.order_management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {

        this.userService = userService;
        this.userMapper = userMapper;

    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {

        User created = userService.create(request.getEmail(), request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponse(created));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<UserResponse>> list(@PageableDefault(page = 0, size = 20, sort = "id") Pageable pageable) {
        Page<User> page = userService.list(pageable);
        List<UserResponse> items = page.getContent().stream().map(userMapper::toResponse).toList();
        PagedResponse<UserResponse> body = new PagedResponse<>(items, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.hasNext(), page.hasPrevious());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id){
        User user = userService.getById(id);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }
}
