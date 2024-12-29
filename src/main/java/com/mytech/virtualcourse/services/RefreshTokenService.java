package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.repositories.RefreshTokenRepository;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.RefreshToken;
import com.mytech.virtualcourse.exceptions.TokenRefreshException;
import com.mytech.virtualcourse.repositories.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);
    @Value("${virtualcourse.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private AccountRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        Account user = userRepository.findById(userId).get();
        Optional<RefreshToken> ifRefreshToken = refreshTokenRepository.findByUser(user);

        if (ifRefreshToken.isPresent()) return ifRefreshToken.get();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        try {
            refreshToken = refreshTokenRepository.save(refreshToken);
        } catch (Exception e) {
            log.error("Error creating refresh token: {}", e.getMessage());
        }

        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
