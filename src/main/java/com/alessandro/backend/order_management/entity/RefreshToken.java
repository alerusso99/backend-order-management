package com.alessandro.backend.order_management.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false,  unique = true, length = 128)
    private String tokenHash;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked = false;

    protected RefreshToken() {}

    public RefreshToken(String username, String tokenHash, Instant expiresAt) {
        this.username = username;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.revoked = false;
    }

    public Long getId() {return id;}
    public String getUsername() {return username;}
    public String getTokenHash() {return tokenHash;}
    public Instant getExpiresAt() {return expiresAt;}
    public boolean isRevoked() {return revoked;}
    public void revoke() {this.revoked = true;}


}
