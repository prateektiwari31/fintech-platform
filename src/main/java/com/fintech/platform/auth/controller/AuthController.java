package com.fintech.platform.auth.controller;

import com.fintech.platform.auth.dto.request.LoginRequest;
import com.fintech.platform.auth.dto.request.RegisterRequest;
import com.fintech.platform.auth.dto.response.AuthResponse;
import com.fintech.platform.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(
            @Valid @RequestBody RegisterRequest request) {

        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request) {

        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refreshToken(
            @RequestParam String refreshToken) {

        return authService.refreshToken(refreshToken);
    }

    @PostMapping("/logout")
    public String logout(
            @RequestParam String refreshToken) {

        authService.logout(refreshToken);

        return "Logout Successful";
    }
}