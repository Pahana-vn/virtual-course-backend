package com.mytech.virtualcourse.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;

import com.mytech.virtualcourse.security.AccountDetailsImpl;

import java.io.IOException;
import java.nio.file.*;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${virtualcourse.app.jwtSecretFilePath:./jwt-secret.key}")
    private String jwtSecretFilePath;

    @Value("${virtualcourse.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    private Key signingKey;

    @PostConstruct
    private void init() {
        try {
            Path path = Paths.get(jwtSecretFilePath);
            if (Files.exists(path)) {
                // Đọc khóa từ file
                byte[] keyBytes = Files.readAllBytes(path);
                String jwtSecretBase64 = new String(keyBytes).trim();
                this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretBase64));
            } else {
                // Tạo khóa mới
                Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
                // Mã hóa khóa thành chuỗi Base64
                String jwtSecretBase64 = Encoders.BASE64.encode(key.getEncoded());
                // Lưu khóa vào file
                Files.write(path, jwtSecretBase64.getBytes(), StandardOpenOption.CREATE_NEW);
                this.signingKey = key;
                System.out.println("Generated and saved new JWT secret key at: " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize JWT secret key", e);
        }
    }

    public String generateJwtToken(Authentication authentication) {
        AccountDetailsImpl userPrincipal = (AccountDetailsImpl) authentication.getPrincipal();

        String roles = userPrincipal.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(userPrincipal.getEmail())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException e) {
            System.err.println("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT claims string is empty: " + e.getMessage());
        }

        return false;
    }
}
