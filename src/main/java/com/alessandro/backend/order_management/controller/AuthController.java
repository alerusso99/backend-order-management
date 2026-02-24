package com.alessandro.backend.order_management.controller;

import com.alessandro.backend.order_management.dto.AuthResponse;
import com.alessandro.backend.order_management.dto.LoginRequest;
import com.alessandro.backend.order_management.dto.LoginResponse;
import com.alessandro.backend.order_management.dto.RefreshRequest;
import com.alessandro.backend.order_management.service.JwtService;
import com.alessandro.backend.order_management.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails user = (UserDetails) auth.getPrincipal();

        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        String oldRefresh = request.getRefreshToken();

        String newRefresh = refreshTokenService.rotateRefreshToken(oldRefresh);
        String username = refreshTokenService.getUsernameFromRefreshToken(newRefresh);

        UserDetails user = org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("")
                .authorities("ROLE_USER")
                .build();

        String newAccess = jwtService.generateToken(user);

        return ResponseEntity.ok(new AuthResponse(newAccess, newRefresh));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        refreshTokenService.revoke(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}