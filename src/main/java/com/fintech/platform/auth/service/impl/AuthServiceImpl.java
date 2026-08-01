package com.fintech.platform.auth.service.impl;

import com.fintech.platform.auth.dto.request.LoginRequest;
import com.fintech.platform.auth.dto.request.RegisterRequest;
import com.fintech.platform.auth.dto.response.AuthResponse;
import com.fintech.platform.auth.repository.RefreshTokenRepository;
import com.fintech.platform.auth.repository.RoleRepository;
import com.fintech.platform.auth.repository.UserRoleRepository;
import com.fintech.platform.auth.service.AuthService;
import com.fintech.platform.common.exception.BadRequestException;
import com.fintech.platform.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final UserRoleRepository userRoleRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered.");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestException("Phone number already registered.");
        }

        return null;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        return null;
    }
}