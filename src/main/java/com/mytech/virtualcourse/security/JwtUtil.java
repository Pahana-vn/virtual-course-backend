//package com.mytech.virtualcourse.security;
//
//import io.jsonwebtoken.*;
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//import java.util.Date;
//
//@Component
//public class JwtUtil {
//
//    @Value("${jwt.secret}")
//    private String jwtSecret;
//
//    @Value("${jwt.expirationMs}")
//    private long jwtExpirationMs;
//
//    public String generateJwtToken(CustomUserDetails userDetails) {
//        Map<String, Object> claims = new HashMap<>();
//        Collection<?> roles = userDetails.getAuthorities();
//        claims.put("roles", roles);
//
//        return Jwts.builder()
//                .setSubject(userDetails.getUsername())
//                .addClaims(claims)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
//                .signWith(SignatureAlgorithm.HS512, jwtSecret)
//                .compact();
//    }
//
//    // Get username from JWT
//    public String getUsernameFromJwtToken(String token) {
//        return Jwts.parser()
//                .setSigningKey(jwtSecret)
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }
//
//    public boolean validateJwtToken(String authToken) {
//        try {
//            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
//            return true;
//        } catch (SignatureException e) {
//            // Invalid JWT signature
//            // Log hoặc xử lý theo ý bạn
//        } catch (MalformedJwtException e) {
//            // Invalid JWT token
//        } catch (ExpiredJwtException e) {
//            // JWT token is expired
//        } catch (UnsupportedJwtException e) {
//            // JWT token is unsupported
//        } catch (IllegalArgumentException e) {
//            // JWT claims string is empty
//        }
//
//        return false;
//    }
//    @PostConstruct
//    public void init() {
//        System.out.println("Decoded JWT Secret Key: " + Arrays.toString(jwtSecret.getEncoded()));
//    }
//}
// src/main/java/com/mytech/virtualcourse/security/JwtUtil.java
package com.mytech.virtualcourse.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key jwtSigningKey;

    @Autowired
    public JwtUtil(Key jwtSigningKey) {
        this.jwtSigningKey = jwtSigningKey;
    }

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    public String generateJwtToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        Collection<?> roles = userDetails.getAuthorities();
        claims.put("roles", roles);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(jwtSigningKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // Get username from JWT
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSigningKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtSigningKey)
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (JwtException e) {
            // Log lỗi hoặc xử lý theo ý bạn
            System.err.println("JWT validation error: " + e.getMessage());
        }
        return false;
    }
}
