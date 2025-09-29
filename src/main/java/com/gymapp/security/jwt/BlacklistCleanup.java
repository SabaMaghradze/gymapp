package com.gymapp.security.jwt;

import com.gymapp.repository.BlacklistedTokenRepository;
import com.gymapp.service.BlacklistedTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class BlacklistCleanup {

    private final BlacklistedTokenRepository repo;

    @Scheduled(cron = "0 0 * * * ?")
    public void cleanup() {
        repo.deleteByExpiryDateBefore(Instant.now());
    }
}
