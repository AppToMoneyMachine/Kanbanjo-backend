package com.bliutvikler.bliutvikler.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BlacklistedTokenService {

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    public void blacklistToken(String token, LocalDateTime expiryDate) {
        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(token);
        blacklistedToken.setExpiryDate(expiryDate);
        blacklistedTokenRepository.save(blacklistedToken);
    }

    public boolean isTokenBlacklisted(String token) {
        Optional<BlacklistedToken> blacklistedToken = blacklistedTokenRepository.findByToken(token);
        return blacklistedToken.isPresent() && blacklistedToken.get().getExpiryDate().isAfter(LocalDateTime.now());
    }
}
