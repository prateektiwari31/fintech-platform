package com.fintech.platform.auth.service;

import com.fintech.platform.auth.dto.request.LoginRequest;
import com.fintech.platform.auth.dto.request.RegisterRequest;
import com.fintech.platform.auth.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(String refreshToken);

    void logout(String refreshToken);
}