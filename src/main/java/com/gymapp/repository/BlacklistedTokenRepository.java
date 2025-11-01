package com.gymapp.repository;

import com.gymapp.model.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    boolean existsByTokenHash(String tokenHash);

    void deleteByExpiryDateBefore(Instant before);
}
