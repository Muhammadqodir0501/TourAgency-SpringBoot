package org.example.touragency.controller;

import lombok.RequiredArgsConstructor;
import org.example.touragency.dto.request.LoginRequest;
import org.example.touragency.dto.request.LogoutRequest;
import org.example.touragency.dto.request.RefreshTokenRequest;
import org.example.touragency.exception.ApiResponse;
import org.example.touragency.model.entity.RefreshToken;
import org.example.touragency.model.entity.User;
import org.example.touragency.security.jwt.JwtUtil;
import org.example.touragency.security.user.CustomUserDetails;
import org.example.touragency.service.abstractions.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        String accessToken = JwtUtil.generateToken(
                userDetails.getId(),
                userDetails.getUser().getRole().name()
        );

        RefreshToken refreshToken =
                refreshTokenService.create(userDetails.getUser());

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken.getToken()
        );
    }


    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestBody RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenService.verify(request.getRefreshToken());

        User user = refreshToken.getUser();

        String accessToken = JwtUtil.generateToken(
                user.getId(),
                user.getRole().name()
        );

        return Map.of("accessToken", accessToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>>  logout(@RequestBody LogoutRequest request) {
        refreshTokenService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }


}
