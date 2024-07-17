package com.bliutvikler.bliutvikler.jwt;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private LocalDateTime expiryDate;

    public BlacklistedToken() {

    };

    public BlacklistedToken(Long id, String token, LocalDateTime expiryDate) {
        this.id = id;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    // getters
    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    // setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
