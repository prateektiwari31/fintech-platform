package com.fintech.platform.auth.service.impl;

import com.fintech.platform.auth.dto.request.LoginRequest;
import com.fintech.platform.auth.dto.request.RegisterRequest;
import com.fintech.platform.auth.dto.response.AuthResponse;
import com.fintech.platform.auth.entity.RefreshToken;
import com.fintech.platform.auth.entity.Role;
import com.fintech.platform.auth.entity.UserRole;
import com.fintech.platform.auth.repository.RefreshTokenRepository;
import com.fintech.platform.auth.repository.RoleRepository;
import com.fintech.platform.auth.repository.UserRoleRepository;
import com.fintech.platform.auth.service.AuthService;
import com.fintech.platform.common.enums.UserStatus;
import com.fintech.platform.common.exception.BadRequestException;
import com.fintech.platform.user.entity.User;
import com.fintech.platform.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.fintech.platform.auth.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists.");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestException("Phone number already exists.");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);

        Role role = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() ->
                        new BadRequestException("Default role not found."));

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();

        userRoleRepository.save(userRole);

        String accessToken =
                jwtService.generateAccessToken(
                        user.getId(),
                        user.getEmail());

        String refreshToken =
                jwtService.generateRefreshToken(
                        user.getId());

        RefreshToken token = RefreshToken.builder()
                .token(refreshToken)
                .expiryDate(
                        LocalDateTime.now()
                                .plusSeconds(jwtService.getRefreshTokenExpiration() / 1000)
                )
                .revoked(false)
                .user(user)
                .build();

        refreshTokenRepository.save(token);

        refreshTokenRepository.save(token);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {

        // 1. Authenticate email & password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Fetch user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadRequestException("User not found."));

        // 3. Remove previous refresh tokens
        refreshTokenRepository.deleteByUser(user);

        // 4. Generate new tokens
        String accessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getEmail()
        );

        String refreshToken = jwtService.generateRefreshToken(
                user.getId()
        );

        // 5. Save refresh token
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .expiryDate(
                        LocalDateTime.now()
                                .plusSeconds(jwtService.getRefreshTokenExpiration() / 1000)
                )
                .revoked(false)
                .user(user)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

        // 6. Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // 7. Return response
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .build();
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {

        // 1. Find Refresh Token
        RefreshToken savedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() ->
                        new BadRequestException("Invalid refresh token."));

        // 2. Check Revoked
        if (Boolean.TRUE.equals(savedToken.getRevoked())) {
            throw new BadRequestException("Refresh token has been revoked.");
        }

        // 3. Check Expiry
        if (savedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Refresh token has expired.");
        }

        // 4. Validate JWT
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new BadRequestException("Invalid refresh token.");
        }

        // 5. User
        User user = savedToken.getUser();

        // 6. Generate New Access Token
        String accessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getEmail()
        );

        // 7. Return
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .build();
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() ->
                        new BadRequestException("Refresh token not found."));

        token.setRevoked(true);

        refreshTokenRepository.save(token);
    }
}