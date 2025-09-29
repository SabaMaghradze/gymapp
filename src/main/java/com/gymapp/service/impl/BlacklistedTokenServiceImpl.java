package com.gymapp.service.impl;

import com.gymapp.model.BlacklistedToken;
import com.gymapp.repository.BlacklistedTokenRepository;
import com.gymapp.security.jwt.JwtUtil;
import com.gymapp.security.jwt.TokenHashUtil;
import com.gymapp.service.BlacklistedTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class BlacklistedTokenServiceImpl implements BlacklistedTokenService {

    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void blacklistToken(String token) {

        String hash = TokenHashUtil.sha256(token);

        // already blacklisted?
        if (blacklistedTokenRepository.existsByTokenHash(hash)) return;

        // extract token expiry from your JwtUtil (adjust method name to your implementation)
        Date expiration = jwtUtil.getExpirationDateFromToken(token);
        Instant expiryInstant = (expiration != null) ? expiration.toInstant() : Instant.now().plusSeconds(3600);

        BlacklistedToken bt = BlacklistedToken.builder()
                .tokenHash(hash)
                .expiryDate(expiryInstant)
                .build();

        blacklistedTokenRepository.save(bt);
    }
}
