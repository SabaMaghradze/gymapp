package com.gymapp.repository;

import com.gymapp.model.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    boolean existsByTokenHash(String tokenHash);

    void deleteByExpiryDateBefore(Instant before);
}
