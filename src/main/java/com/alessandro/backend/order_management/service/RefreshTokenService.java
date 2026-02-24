package com.alessandro.backend.order_management.service;

import com.alessandro.backend.order_management.entity.RefreshToken;
import com.alessandro.backend.order_management.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final long refreshExpirationDays;

    public RefreshTokenService(
            RefreshTokenRepository repo,
            @Value("${security.jwt.refresh-expiration-days}") long refreshExpirationDays
    ) {
        this.repo = repo;
        this.refreshExpirationDays = refreshExpirationDays;
    }

    public String createRefreshToken(String username) {
        String raw = UUID.randomUUID() + "." + UUID.randomUUID(); // abbastanza random
        String hash = sha256Base64(raw);

        Instant expiresAt = Instant.now().plus(refreshExpirationDays, ChronoUnit.DAYS);
        repo.save(new RefreshToken(username, hash, expiresAt));
        return raw;
    }

    public String rotateRefreshToken(String rawToken) {
        RefreshToken existing = validateAndGet(rawToken);
        existing.revoke();
        repo.save(existing);

        return createRefreshToken(existing.getUsername());
    }

    public String getUsernameFromRefreshToken(String rawToken) {
        return validateAndGet(rawToken).getUsername();
    }

    public void revoke(String rawToken) {
        String hash = sha256Base64(rawToken);
        repo.findByTokenHash(hash).ifPresent(rt -> {
            rt.revoke();
            repo.save(rt);
        });
    }

    private RefreshToken validateAndGet(String rawToken) {
        String hash = sha256Base64(rawToken);

        RefreshToken rt = repo.findByTokenHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (rt.isRevoked()) {
            throw new IllegalArgumentException("Refresh token revoked");
        }
        if (rt.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Refresh token expired");
        }
        return rt;
    }

    private String sha256Base64(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception e) {
            throw new RuntimeException("Cannot hash token", e);
        }
    }
}
