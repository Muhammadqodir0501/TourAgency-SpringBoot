package org.example.touragency.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.touragency.model.entity.RefreshToken;
import org.example.touragency.model.entity.User;
import org.example.touragency.repository.RefreshTokenRepository;
import org.example.touragency.service.abstractions.RefreshTokenService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private static final long REFRESH_TOKEN_DAYS = 30;

    @Override
    public RefreshToken create(User user) {
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plus(REFRESH_TOKEN_DAYS, ChronoUnit.DAYS))
                .build();

        return  refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verify(String token) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(()-> new RuntimeException("Invalid refresh token"));

        if(refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.deleteByUser(refreshToken.getUser());
            throw new RuntimeException("Refresh token expired");
        }
        return refreshToken;
    }

    @Override
    public void logout(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(()-> new RuntimeException("Invalid refresh token"));

        refreshTokenRepository.deleteByUser(refreshToken.getUser());
    }
}
